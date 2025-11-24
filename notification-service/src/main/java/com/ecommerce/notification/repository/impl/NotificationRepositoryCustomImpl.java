package com.ecommerce.notification.repository.impl;

import com.ecommerce.notification.repository.NotificationRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryCustomImpl implements NotificationRepositoryCustom {

    private final MongoTemplate mongoTemplate;
    @Override
    public void markAllAsReadByUserId(Long userId) {
        Query query = new Query(Criteria.where("userId").is(userId).and("isRead").is(false));
        Update update = new Update().set("isRead", true);
        mongoTemplate.updateMulti(query, update, "notifications");
    }
}

