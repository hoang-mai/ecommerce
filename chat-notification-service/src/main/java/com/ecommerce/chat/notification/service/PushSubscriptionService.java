package com.ecommerce.chat.notification.service;

import com.ecommerce.chat.notification.dto.NotificationDto;
import com.ecommerce.chat.notification.dto.PushSubscriptionRequest;


public interface PushSubscriptionService {

    /**
     * Subscribe nhận push notification
     */
    void subscribe(PushSubscriptionRequest request);

    /**
     * Unsubscribe từ push notification
     */
    void unsubscribe(String endpoint);

    /**
     * Gửi push notification đến tất cả subscription active của user
     *
     * @param userId      ID của user
     * @param notification Nội dung notification
     */
    void sendNotificationToUser(Long userId, NotificationDto notification);
}
