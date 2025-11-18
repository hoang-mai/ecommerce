package com.ecommerce.notification.service;

import com.ecommerce.notification.dto.PushSubscriptionDto;
import com.ecommerce.notification.dto.PushSubscriptionRequest;

import java.util.List;

public interface PushSubscriptionService {

    /**
     * Subscribe nhận push notification
     */
    PushSubscriptionDto subscribe(PushSubscriptionRequest request);

    /**
     * Unsubscribe từ push notification
     */
    void unsubscribe(String endpoint);

    /**
     * Lấy tất cả subscription của user
     */
    List<PushSubscriptionDto> getUserSubscriptions();

    /**
     * Lấy tất cả subscription đang active của user
     */
    List<PushSubscriptionDto> getActiveSubscriptions();

    /**
     * Deactivate một subscription
     */
    void deactivateSubscription(Long subscriptionId);

    /**
     * Activate một subscription
     */
    void activateSubscription(Long subscriptionId);
}

