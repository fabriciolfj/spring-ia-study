package com.example.study.chatmemory;

public record ConversationMessage(
        String messageType,
        String content) {
}