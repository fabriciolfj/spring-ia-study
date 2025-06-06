package com.example.study.service;

import com.example.study.entity.Game;
import com.example.study.exceptions.AnswerNotRelevantException;
import com.example.study.model.Answer;
import com.example.study.model.Question;
import com.example.study.tools.GameTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class BoardGameServiceImpl implements BoardGameService {

    private static final Logger log = LoggerFactory.getLogger(BoardGameServiceImpl.class);

    @Value("classpath:/promptTemplates/systemPromptTemplate.st")
    private Resource promptTemplate;

    private final ChatClient chatClient;
    private final GameTools gameTools;

    public BoardGameServiceImpl(final ChatClient chatClient, final GameTools gameTools) {
        this.gameTools = gameTools;
        this.chatClient = chatClient;
    }

    @Override
    @Retryable(retryFor = AnswerNotRelevantException.class, maxAttempts = 3)
    public Answer askQuestion(Question question, String conversationId) {
        String gameNameMatch = String.format(
                "gameTitle == '%s'",
                normalizeGameTitle(question.gameTitle()));

        var response = chatClient.prompt()
                .system(userSpec -> userSpec
                        .text(promptTemplate)
                        .param("gameTitle", question.gameTitle())
                        .param("question_answer_context", gameNameMatch)
                        )
                .user(question.question())
                .advisors(advisorSpec -> advisorSpec.param(QuestionAnswerAdvisor.FILTER_EXPRESSION, gameNameMatch).
                            param(ChatMemory.CONVERSATION_ID, conversationId)
                        )
                .tools(gameTools)
                .call()
                .responseEntity(Answer.class);

        log.info(response.entity().answer());

        var metadata = response.getResponse().getMetadata();
        logUsage(metadata.getUsage());

        return response.entity();
    }

    @Recover
    public Answer recover(AnswerNotRelevantException e) {
        log.warn(e.getMessage());
        return new Answer("", "I'm sorry, I wasn't able to answer the question.");
    }

    private void logUsage(Usage usage) {
        log.info("Token usage: prompt={}, generation={}, total={}",
                usage.getPromptTokens(),
                usage.getCompletionTokens(),
                usage.getTotalTokens());
    }

    private String normalizeGameTitle(final String gameTitle) {
        return gameTitle.toLowerCase().replace(" ", "_");
    }
}
