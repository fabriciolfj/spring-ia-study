package com.example.study.config;

import com.example.study.chatmemory.MongoChatMemory;
import com.example.study.repository.ConversationRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.ChatModelCallAdvisor;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    ChatMemory chatMemory(ConversationRepository repository) {
        return new MongoChatMemory(repository);
    }

    @Bean
    ChatClient chatClient(
            ChatClient.Builder chatClientBuilder,
            VectorStore vectorStore,
            ChatMemory chatMemory) {
        return chatClientBuilder
                .defaultAdvisors(
                        PromptChatMemoryAdvisor.builder(chatMemory).build(),
                        QuestionAnswerAdvisor.builder(vectorStore).searchRequest(SearchRequest.builder().build()).build())
                .build();
    }
}
