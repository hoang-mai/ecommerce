package com.ecommerce.chat.repository;

import com.ecommerce.chat.entity.ChatMember;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatMemberRepository extends MongoRepository<ChatMember,String> {


    Optional<ChatMember> findByChatIdAndUserIdAndIsDeleted(String chatId, Long senderId,Boolean isDeleted);

    Optional<ChatMember> findByChatIdAndUserId(String chatId, Long userId);
}
