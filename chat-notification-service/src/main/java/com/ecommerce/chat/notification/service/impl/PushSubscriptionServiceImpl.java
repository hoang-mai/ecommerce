package com.ecommerce.chat.notification.service.impl;

import com.ecommerce.chat.notification.dto.NotificationDto;
import com.ecommerce.chat.notification.service.PushSubscriptionService;
import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.chat.notification.dto.PushSubscriptionRequest;
import com.ecommerce.chat.notification.entity.PushSubscription;
import com.ecommerce.chat.notification.repository.PushSubscriptionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PushSubscriptionServiceImpl implements PushSubscriptionService {

    private static final Logger log = LoggerFactory.getLogger(PushSubscriptionServiceImpl.class);

    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final UserHelper userHelper;
    private final PushService pushService;
    private final ObjectMapper objectMapper;

    @Override
    public void subscribe(PushSubscriptionRequest request) {
        Long userId = userHelper.getCurrentUserId();

        if (pushSubscriptionRepository.existsByEndpointAndUserId(request.getEndpoint(), userId)) {
            PushSubscription existing = pushSubscriptionRepository.findByEndpoint(request.getEndpoint())
                    .orElseThrow(() -> new NotFoundException("Subscription không tồn tại"));
            existing.setActive(true);
            pushSubscriptionRepository.save(existing);
            return;
        }

        // Tạo mới subscription
        PushSubscription pushSubscription = PushSubscription.builder()
                .userId(userId)
                .endpoint(request.getEndpoint())
                .p256dh(request.getKeys().getP256dh())
                .auth(request.getKeys().getAuth())
                .active(true)
                .build();

        pushSubscriptionRepository.save(pushSubscription);
    }

    @Override
    public void unsubscribe(String endpoint) {
        PushSubscription pushSubscription = pushSubscriptionRepository.findByEndpoint(endpoint)
                .orElseThrow(() -> new NotFoundException("Subscription không tồn tại"));
        pushSubscriptionRepository.deleteById(pushSubscription.get_id());
    }

    @Override
    public void sendNotificationToUser(Long userId, NotificationDto notification) {
        List<PushSubscription> subscriptions = pushSubscriptionRepository
                .findPushSubscriptionByUserIdAndActive(userId, true);

        if (subscriptions.isEmpty()) {
            log.debug("No active subscriptions found for user: {}", userId);
            return;
        }

        for (PushSubscription subscription : subscriptions) {
            try {
                sendPushNotification(subscription, notification);
                log.info("Successfully sent push notification to user: {}, subscription: {}", userId, subscription.get_id());
            } catch (Exception e) {
                log.error("Failed to send notification to subscription {}: {}", subscription.get_id(), e.getMessage());

                // Deactivate subscription if it fails (endpoint might be invalid)
                if (e.getMessage() != null && e.getMessage().contains("410")) {
                    subscription.setActive(false);
                    pushSubscriptionRepository.save(subscription);
                    log.info("Deactivated invalid subscription: {}", subscription.get_id());
                }
            }
        }
    }

    private void sendPushNotification(PushSubscription subscription, NotificationDto notification) throws Exception {
        // Build Web Push Subscription
        Subscription webPushSubscription = new Subscription(
                subscription.getEndpoint(),
                new Subscription.Keys(subscription.getP256dh(), subscription.getAuth())
        );

        // Build notification payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", notification.getTitle());
        payload.put("message", notification.getMessage());
        payload.put("data", notification.getData());
        payload.put("timestamp", System.currentTimeMillis());

        String payloadJson = objectMapper.writeValueAsString(payload);

        // Send notification
        Notification webPushNotification = new Notification(webPushSubscription, payloadJson);
        pushService.send(webPushNotification);

        log.debug("Push notification sent to subscription: {}", subscription.get_id());
    }

}

