package com.ecommerce.chat.repository;

import com.ecommerce.chat.entity.UserChat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserChatRepository extends MongoRepository<UserChat,String> {

    Optional<UserChat> findUserChatByChatIdAndUserId(String chatId, String userId);

    Optional<UserChat> findUserChatByChatIdAndUserIdNot(String chatId, String userId);

    /**
     * Đếm số lượng userChat chưa đọc của một người dùng
     */
    long countByUserIdAndIsReadFalse(String userId);

}
