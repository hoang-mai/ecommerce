package com.ecommerce.chat.notification.service.impl;

import com.ecommerce.chat.notification.dto.NotificationDto;
import com.ecommerce.chat.notification.service.OrderService;
import com.ecommerce.chat.notification.service.PushSubscriptionService;
import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.enumeration.OrderStatus;
import com.ecommerce.library.kafka.event.order.CreateListOrderStatusEvent;
import com.ecommerce.library.kafka.event.order.OrderStatusEvent;
import com.ecommerce.library.kafka.event.payment.CreatePaymentEvent;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.chat.notification.entity.Notification;
import com.ecommerce.chat.notification.entity.NotificationType;
import com.ecommerce.chat.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationRepository notificationRepository;
    private final MessageService messageService;
    private final RedisTemplate<String, String> redisTemplate;
    private final PushSubscriptionService pushSubscriptionService;

    @Override
    public void sendOrderStatusMessage(CreateListOrderStatusEvent createListOrderStatusEvent) {
        if (createListOrderStatusEvent == null || createListOrderStatusEvent.getOrderStatusEventList().isEmpty()) {
            return;
        }
        boolean hasCancelled = false;
        for (var event : createListOrderStatusEvent.getOrderStatusEventList()) {
            if (OrderStatus.CANCELLED.equals(event.getOrderStatus())) {
                hasCancelled = true;
                continue;
            }
            Notification notification = Notification.builder()
                .userId(event.getOwnerId())
                .notificationType(NotificationType.INFO)
                .isRead(false)
                .sentRealtime(true)
                .title(messageService.getMessage(MessageSuccess.NEW_ORDER))
                .message(messageService.getMessage(MessageSuccess.NEW_ORDER_MESSAGE))
                .build();
            notificationRepository.save(notification);
            if (isUserOnline(String.valueOf(event.getOwnerId()))) {
                simpMessagingTemplate.convertAndSendToUser(
                    String.valueOf(event.getOwnerId()),
                    "/queue/notify",
                    notification
                );
            } else {
                NotificationDto notificationDto = NotificationDto.builder()
                    .notificationId(notification.get_id())
                    .userId(notification.getUserId())
                    .title(notification.getTitle())
                    .message(notification.getMessage())
                    .notificationType(notification.getNotificationType())
                    .data(Map.of(
                        "url","owner/orders",
                        "notificationId", notification.get_id(),
                        "notificationType", notification.getNotificationType()
                    ))
                    .isRead(notification.getIsRead())
                    .sentRealtime(notification.getSentRealtime())
                    .createdAt(notification.getCreatedAt())
                    .updatedAt(notification.getUpdatedAt())
                    .build();
                pushSubscriptionService.sendNotificationToUser(event.getOwnerId(), notificationDto);
            }
        }


        NotificationType type = hasCancelled ? NotificationType.ERROR : NotificationType.SUCCESS;
        String title = messageService.getMessage(hasCancelled ? MessageSuccess.ORDER_CANCELLED_TITLE : MessageSuccess.ORDER_SUCCESS_TITLE);
        String message = messageService.getMessage(hasCancelled ? MessageSuccess.ORDER_CANCELLED_MESSAGE : MessageSuccess.ORDER_SUCCESS_MESSAGE);


        Long userId = createListOrderStatusEvent.getUserId();
        Notification notification = Notification.builder()
            .userId(userId)
            .notificationType(type)
            .isRead(false)
            .sentRealtime(true)
            .title(title)
            .message(message)
            .build();
        notificationRepository.save(notification);
        simpMessagingTemplate.convertAndSendToUser(
            String.valueOf(userId),
            "/queue/notify",
            notification
        );
    }

    @Override
    public void sendPaymentNotificationMessage(CreatePaymentEvent createPaymentEvent) {
        if (!FnCommon.isNotNull(createPaymentEvent)) return;
        Long userId = createPaymentEvent.getUserId();
        Notification notification = Notification.builder()
            .userId(userId)
            .notificationType(NotificationType.PAYMENT)
            .isRead(false)
            .sentRealtime(true)
            .message(createPaymentEvent.getPaymentUrl())
            .build();
        simpMessagingTemplate.convertAndSendToUser(
            String.valueOf(userId),
            "/queue/notify",
            notification
        );

    }

    @Override
    public void sendOrderStatusUpdateNotification(OrderStatusEvent orderStatusEvent) {
        if (orderStatusEvent == null || orderStatusEvent.getUserId() == null) {
            return;
        }

        Long userId = orderStatusEvent.getUserId();
        OrderStatus orderStatus = orderStatusEvent.getOrderStatus();

        // Determine notification type and messages based on order status
        NotificationType notificationType;
        String titleKey;
        String messageKey;

        if (orderStatus == OrderStatus.COMPLETED) {
            notificationType = NotificationType.SUCCESS;
            titleKey = MessageSuccess.ORDER_COMPLETED_TITLE;
            messageKey = MessageSuccess.ORDER_COMPLETED_MESSAGE;
        } else if (orderStatus == OrderStatus.CANCELLED || orderStatus == OrderStatus.RETURNED) {
            notificationType = NotificationType.ERROR;
            titleKey = MessageSuccess.ORDER_CANCELLED_TITLE;
            messageKey = MessageSuccess.ORDER_CANCELLED_MESSAGE;
        } else {
            notificationType = NotificationType.INFO;
            titleKey = MessageSuccess.ORDER_STATUS_UPDATED_TITLE;
            messageKey = MessageSuccess.ORDER_STATUS_UPDATED_MESSAGE;
        }

        String title = messageService.getMessage(titleKey);
        String message = messageService.getMessage(messageKey, orderStatusEvent.getOrderId(), orderStatus.getValueVi());

        // Create and save notification
        Notification notification = Notification.builder()
            .userId(userId)
            .notificationType(notificationType)
            .isRead(false)
            .sentRealtime(true)
            .title(title)
            .message(message)
            .build();
        notificationRepository.save(notification);

        // Send notification via WebSocket if user is online
        if (isUserOnline(String.valueOf(userId))) {
            simpMessagingTemplate.convertAndSendToUser(
                String.valueOf(userId),
                "/queue/notify",
                notification
            );
        } else {
            // Send push notification if user is offline
            NotificationDto notificationDto = NotificationDto.builder()
                .notificationId(notification.get_id())
                .userId(notification.getUserId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .notificationType(notification.getNotificationType())
                .isRead(notification.getIsRead())
                .data(Map.of(
                    "url","/",
                    "notificationId", notification.get_id(),
                    "notificationType", notification.getNotificationType()
                ))
                .sentRealtime(notification.getSentRealtime())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build();
            pushSubscriptionService.sendNotificationToUser(userId, notificationDto);
        }
    }

    /**
     * Kiểm tra user có online không bằng cách check key trong Redis
     */
    private boolean isUserOnline(String userId) {
        try {
            String userKey = "chat:user:" + userId;
            return redisTemplate.hasKey(userKey);
        } catch (Exception e) {
            return false;
        }
    }
}
