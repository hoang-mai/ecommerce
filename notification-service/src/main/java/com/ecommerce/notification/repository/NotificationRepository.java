package com.ecommerce.notification.repository;

import com.ecommerce.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String>, NotificationRepositoryCustom {
    Page<Notification> findByUserId(Long userId, Pageable pageable);

    long countByUserIdAndIsRead(Long userId, Boolean isRead);
}
