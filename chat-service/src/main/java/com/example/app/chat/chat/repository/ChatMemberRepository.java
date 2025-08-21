package com.example.app.chat.chat.repository;

import com.example.app.chat.chat.entity.ChatMember;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatMemberRepository extends MongoRepository<ChatMember,String> {

    Optional<ChatMember> findByChatIdAndUserId(String chatId, Long userId);
}
