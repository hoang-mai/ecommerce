package com.ecommerce.chat.notification.repository.impl;

import com.ecommerce.chat.notification.entity.Notification;
import com.ecommerce.chat.notification.entity.NotificationType;
import com.ecommerce.library.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl {

    private final MongoTemplate mongoTemplate;

    public Page<Notification> findByUserId(Long userId, String keyword, NotificationType notificationType, Boolean isRead, Pageable pageable) {

        List<Criteria> criteria = new ArrayList<>();

        criteria.add(Criteria.where("userId").is(userId));

        if (FnCommon.isNotNullOrEmpty(keyword)) {
            Criteria keywordCriteria = new Criteria().orOperator(
                Criteria.where("title").regex(keyword, "i"),
                Criteria.where("message").regex(keyword, "i")
            );
            criteria.add(keywordCriteria);
        }

        if (FnCommon.isNotNull(notificationType)) {
            criteria.add(Criteria.where("notificationType").is(notificationType));
        }

        if (FnCommon.isNotNull(isRead)) {
            criteria.add(Criteria.where("isRead").is(isRead));
        }
        Query query = new Query();
        query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        long total = mongoTemplate.count(query, "notifications");

        query.with(pageable);

        List<Notification> notifications = mongoTemplate.find(query, Notification.class);

        return new PageImpl<>(notifications, pageable, total);
    }
}

