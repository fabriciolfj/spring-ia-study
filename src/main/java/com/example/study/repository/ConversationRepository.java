package com.example.study.repository;

import com.example.study.chatmemory.Conversation;
import org.springframework.data.repository.CrudRepository;

public interface ConversationRepository extends CrudRepository<Conversation, String> {
}
