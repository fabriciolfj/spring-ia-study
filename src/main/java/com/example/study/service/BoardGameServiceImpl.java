package com.example.study.service;

import com.example.study.exceptions.AnswerNotRelevantException;
import com.example.study.model.Answer;
import com.example.study.model.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardGameServiceImpl implements BoardGameService {

    private static final Logger log = LoggerFactory.getLogger(BoardGameServiceImpl.class);

    @Value("classpath:/promptTemplates/systemPromptTemplate.st")
    private Resource promptTemplate;

    private final ChatClient chatClient;
    private final RelevancyEvaluator evaluator;
    private final GameRulesService gameRulesService;

    public BoardGameServiceImpl(ChatClient.Builder chatClientBuilder, GameRulesService gameRulesService) {
        ChatOptions chatOptions = ChatOptions.builder()
                .temperature(0.3)
                .build();

        this.chatClient = chatClientBuilder
                .defaultOptions(chatOptions)
                .build();

        this.evaluator  = new RelevancyEvaluator(chatClientBuilder);
        this.gameRulesService = gameRulesService;
    }

    @Override
    @Retryable(retryFor = AnswerNotRelevantException.class, maxAttempts = 3)
    public Answer askQuestion(Question question) {
        final String rules = gameRulesService.getRulesFor(question.gameTitle());
        var answerText = chatClient.prompt()
                .system(userSpec -> userSpec
                        .text(promptTemplate)
                        .param("gameTitle", question.gameTitle())
                        .param("rules", rules))
                .user(question.question())
                .call()
                .entity(Answer.class);

        log.info(answerText.answer());
        evaluateRelevancy(question, answerText.answer());

        return answerText;
    }

    @Recover
    public Answer recover(AnswerNotRelevantException e) {
        log.warn(e.getMessage());
        return new Answer("", "I'm sorry, I wasn't able to answer the question.");
    }

    private void evaluateRelevancy(Question question, String answerText) {
        EvaluationRequest evaluationRequest =
                new EvaluationRequest(question.question(), answerText);
        EvaluationResponse evaluationResponse = evaluator.evaluate(evaluationRequest);
        if (!evaluationResponse.isPass()) {
            log.warn("nao passou");
            //throw new AnswerNotRelevantException(question.question(), answerText); //
        }
    }
}
