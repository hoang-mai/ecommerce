package com.ecommerce.notification.service.impl;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.enumeration.OrderStatus;
import com.ecommerce.library.kafka.event.order.OrderStatusEvent;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.notification.entity.Notification;
import com.ecommerce.notification.entity.NotificationType;
import com.ecommerce.notification.repository.NotificationRepository;
import com.ecommerce.notification.service.OrderStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderStatusServiceImpl implements OrderStatusService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationRepository notificationRepository;
    private final MessageService messageService;

    @Override
    public void sendOrderStatusMessage(List<OrderStatusEvent> orderStatusEventList) {
        if (orderStatusEventList == null || orderStatusEventList.isEmpty()) {
            return;
        }

        boolean hasCancelled = orderStatusEventList.stream()
                .anyMatch(e -> e.getOrderStatus() == OrderStatus.CANCELLED);
        NotificationType type = hasCancelled ? NotificationType.ERROR : NotificationType.SUCCESS;
        String title = messageService.getMessage(hasCancelled ? MessageSuccess.ORDER_CANCELLED_TITLE : MessageSuccess.ORDER_SUCCESS_TITLE);
        String message = messageService.getMessage(hasCancelled ? MessageSuccess.ORDER_CANCELLED_MESSAGE : MessageSuccess.ORDER_SUCCESS_MESSAGE);


        Long userId = orderStatusEventList.get(0).getUserId();
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
}
