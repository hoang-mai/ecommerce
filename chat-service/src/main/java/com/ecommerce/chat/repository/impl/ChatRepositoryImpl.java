package com.ecommerce.chat.repository.impl;

import com.ecommerce.chat.entity.Chat;
import com.ecommerce.library.kafka.event.shop.CreateShopEvent;
import com.ecommerce.library.kafka.event.user.UpdateUserEvent;
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
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatRepositoryImpl {
    private final MongoTemplate mongoTemplate;

    public Page<Chat> findByUserId(Long currentUserId, Pageable pageable, String keyword, String shopId) {
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("userCacheList._id").is(String.valueOf(currentUserId)));
        if (FnCommon.isNotNullOrEmpty(keyword)) {
            criteriaList.add(new Criteria().orOperator(
                Criteria.where("shopCache.shopName").regex(keyword, "i"),
                Criteria.where("userCacheList.fullName").regex(keyword, "i")
            ));
        }
        if (FnCommon.isNotNullOrEmpty(shopId)) {
            criteriaList.add(Criteria.where("shopCache._id").is(shopId));
        }
        Criteria finalCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        Query query = new Query(finalCriteria);
        long total = mongoTemplate.count(query, "chats");
        query.with(pageable);
        List<Chat> chatList = mongoTemplate.find(query, Chat.class, "chats");
        return new PageImpl<>(chatList, pageable, total);
    }

    public Chat findByShopId(String shopId, String userId) {
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("shopCache._id").is(shopId));
        criteriaList.add(Criteria.where("userCacheList._id").is(userId));
        Criteria finalCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        Query query = new Query(finalCriteria);
        return mongoTemplate.findOne(query, Chat.class, "chats");
    }

    public void updateAvatarInChats(Long userId, String avatarUrl) {
        Query query = new Query(Criteria.where("userCacheList._id").is(String.valueOf(userId)));
        Update update = new Update().set("userCacheList.$.avatarUrl", avatarUrl);
        mongoTemplate.updateMulti(query, update, Chat.class);
    }

    public void updateUserInChats(UpdateUserEvent updateUserEvent) {
        Query query = new Query(Criteria.where("userCacheList._id").is(String.valueOf(updateUserEvent.getUserId())));
        Update update = new Update()
            .set("userCacheList.$.fullName", updateUserEvent.getFullName())
            .set("userCacheList.$.email", updateUserEvent.getEmail())
            .set("userCacheList.$.phoneNumber", updateUserEvent.getPhoneNumber());
        mongoTemplate.updateMulti(query, update, Chat.class);
    }

    public void updateShopInChats(CreateShopEvent createShopEvent) {
        Query query = new Query(Criteria.where("shopCache._id").is(String.valueOf(createShopEvent.getShopId())));
        Update update = new Update()
            .set("shopCache.shopName", createShopEvent.getShopName())
            .set("shopCache.logoUrl", createShopEvent.getLogoUrl());
        mongoTemplate.updateMulti(query, update, Chat.class);
    }

    public void markChatAsRead(String chatId, String userId) {
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("_id").is(chatId));
        criteriaList.add(Criteria.where("userCacheList._id").is(userId));
        criteriaList.add(Criteria.where("lastMessage.receiverId").is(userId));
        criteriaList.add(Criteria.where("lastMessage.readBy").ne(userId));
        Criteria finalCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        Query query = new Query(finalCriteria);
        Update update = new Update().addToSet("lastMessage.readBy", userId);
        mongoTemplate.updateMulti(query, update, Chat.class);

    }
}
