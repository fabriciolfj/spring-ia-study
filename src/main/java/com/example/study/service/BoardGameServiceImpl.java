package com.example.study.service;

import com.example.study.model.Answer;
import com.example.study.model.Question;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;

@Service
public class BoardGameServiceImpl implements BoardGameService {

    private final ChatClient chatClient;

    public BoardGameServiceImpl(ChatClient.Builder chatClientBuilder) {
        ChatOptions chatOptions = ChatOptions.builder()
                .model("gpt-4o-mini")
                .build();

        this.chatClient = chatClientBuilder
                .defaultOptions(chatOptions)
                .build();
    }

    @Override
    public Answer askQuestion(Question question) {
        String answerText = chatClient.prompt()
                .user(question.question())
                .call()
                .content();
        return new Answer(answerText);
    }
}
