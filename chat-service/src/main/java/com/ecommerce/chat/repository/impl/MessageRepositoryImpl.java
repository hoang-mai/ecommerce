package com.ecommerce.chat.repository.impl;

import com.ecommerce.chat.entity.Message;
import com.ecommerce.library.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MessageRepositoryImpl {
    private final MongoTemplate mongoTemplate;

    public Page<Message> findByChatId(String chatId, Pageable pageable) {
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("chatId").is(chatId));
        Criteria finalCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        Query query = new Query(finalCriteria);
        long total = mongoTemplate.count(query, "messages");
        query.with(pageable);
        List<Message> messageList = mongoTemplate.find(query, Message.class, "messages");
        return new PageImpl<>(messageList, pageable, total);
    }

    public Long getCountUnreadMessages(String chatId, String userId) {
        List<Criteria> criteriaList = new ArrayList<>();
        if(FnCommon.isNotNullOrEmpty(chatId)){
            criteriaList.add(Criteria.where("chatId").is(chatId));
        }
        criteriaList.add(Criteria.where("receiverId").is(userId));
        criteriaList.add(Criteria.where("readBy").nin(Collections.singletonList(userId)));
        Criteria finalCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        Query query = new Query(finalCriteria);
        return mongoTemplate.count(query, "messages");
    }

    public void markMessagesAsRead(String chatId, String userId) {
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("chatId").is(chatId));
        criteriaList.add(Criteria.where("receiverId").is(userId));
        criteriaList.add(Criteria.where("readBy").nin(Collections.singletonList(userId)));
        Criteria finalCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        Query query = new Query(finalCriteria);
        Update update = new Update().addToSet("readBy", userId);

        mongoTemplate.updateMulti(query, update, Message.class);
    }
}