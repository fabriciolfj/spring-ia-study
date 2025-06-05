package com.example.study.chatmemory;

import com.example.study.repository.ConversationRepository;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MongoChatMemory implements ChatMemory {

    private final ConversationRepository conversationRepository;

    public MongoChatMemory(final ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    @Override
    public void add(final String conversationId, final Message message) {
        ChatMemory.super.add(conversationId, message);
    }

    @Override
    public void add(final String conversationId, final List<Message> messages) {
        List<ConversationMessage> conversationMessages = messages.stream()
                .map(message -> new ConversationMessage(
                        message.getMessageType().getValue(), message.getText()
                )).toList();

        conversationRepository.findById(conversationId)
                .ifPresentOrElse(conversation -> {
                    var existingMessages = conversation.messages();
                    existingMessages.addAll(conversationMessages);
                    conversationRepository.save(new Conversation(conversationId, existingMessages));
                }, () -> conversationRepository.save(new Conversation(conversationId, conversationMessages)));
    }

    @Override
    public List<Message> get(final String conversationId) {
        return conversationRepository
                .findById(conversationId)
                .map(conversation -> {
                    var messageList = conversation.messages().stream()
                            .map(conversationMessage -> {
                                var type = conversationMessage.messageType();
                                Message message = type.equals(MessageType.USER.getValue()) ? new UserMessage(conversationMessage.content()) :
                                        new AssistantMessage(conversationMessage.content());

                                return message;
                            }).toList();
                    return messageList.stream()
                            .toList();
                }).orElse(List.of());
    }

    @Override
    public void clear(final String conversationId) {
        conversationRepository.deleteById(conversationId);
    }
}
