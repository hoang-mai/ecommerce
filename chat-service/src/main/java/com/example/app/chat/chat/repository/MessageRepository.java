package com.example.app.chat.chat.repository;

import com.example.app.chat.chat.dto.ResMessageDTO;
import com.example.app.chat.chat.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {

    @Aggregation()
    Page<ResMessageDTO> findByChatId(String chatId, Pageable pageable);
}
