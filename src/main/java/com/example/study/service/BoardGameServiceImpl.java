package com.example.study.service;

import com.example.study.exceptions.AnswerNotRelevantException;
import com.example.study.model.Answer;
import com.example.study.model.Question;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardGameServiceImpl implements BoardGameService {

    private final ChatClient chatClient;
    private final RelevancyEvaluator evaluator;

    public BoardGameServiceImpl(ChatClient.Builder chatClientBuilder) {
        ChatOptions chatOptions = ChatOptions.builder()
                .temperature(0.7)
                .build();

        this.chatClient = chatClientBuilder
                .defaultOptions(chatOptions)
                .build();

        this.evaluator  = new RelevancyEvaluator(chatClientBuilder);
    }

    @Override
    @Retryable(retryFor = AnswerNotRelevantException.class, maxAttempts = 3)  //
    public Answer askQuestion(Question question) {
        String answerText = chatClient.prompt()
                .user(question.question())
                .call()
                .content();

        evaluateRelevancy(question, answerText);

        return new Answer(answerText);
    }

    @Recover
    public Answer recover(AnswerNotRelevantException e) {
        return new Answer("I'm sorry, I wasn't able to answer the question.");
    }

    private void evaluateRelevancy(Question question, String answerText) {
        EvaluationRequest evaluationRequest =
                new EvaluationRequest(question.question(), List.of(), answerText);
        EvaluationResponse evaluationResponse = evaluator.evaluate(evaluationRequest);
        if (!evaluationResponse.isPass()) {
            throw new AnswerNotRelevantException(question.question(), answerText); //
        }
    }
}
