package com.ecommerce.chat.repository.impl;

import com.ecommerce.chat.entity.Chat;
import com.ecommerce.library.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
}
