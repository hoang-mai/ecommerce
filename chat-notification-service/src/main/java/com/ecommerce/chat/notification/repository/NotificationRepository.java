package com.ecommerce.chat.notification.repository;

import com.ecommerce.chat.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    long countByUserIdAndIsRead(Long userId, Boolean isRead);

    void markAllAsReadByUserId(Long userId);
}
