package com.ecommerce.chat.notification.service.impl;

import com.ecommerce.chat.notification.dto.NotificationDto;
import com.ecommerce.chat.notification.service.OrderStatusService;
import com.ecommerce.chat.notification.service.PushSubscriptionService;
import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.enumeration.OrderStatus;
import com.ecommerce.library.kafka.event.order.CreateListOrderStatusEvent;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.chat.notification.entity.Notification;
import com.ecommerce.chat.notification.entity.NotificationType;
import com.ecommerce.chat.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderStatusServiceImpl implements OrderStatusService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationRepository notificationRepository;
    private final MessageService messageService;
    private final RedisTemplate<String , String> redisTemplate;
    private final PushSubscriptionService pushSubscriptionService;

    @Override
    public void sendOrderStatusMessage(CreateListOrderStatusEvent createListOrderStatusEvent) {
        if (createListOrderStatusEvent == null || createListOrderStatusEvent.getOrderStatusEventList().isEmpty()) {
            return;
        }
        boolean hasCancelled = false;
        for( var event : createListOrderStatusEvent.getOrderStatusEventList()) {
            if(OrderStatus.CANCELLED.equals(event.getOrderStatus())) {
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
            if(isUserOnline(String.valueOf(event.getOwnerId()))) {
                simpMessagingTemplate.convertAndSendToUser(
                        String.valueOf(event.getOwnerId()),
                        "/queue/notify",
                        notification
                );
            }else{
                NotificationDto notificationDto = NotificationDto.builder()
                        .notificationId(notification.get_id())
                        .userId(notification.getUserId())
                        .title(notification.getTitle())
                        .message(notification.getMessage())
                        .notificationType(notification.getNotificationType())
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
