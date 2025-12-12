package com.ecommerce.chat.service.impl;

import com.ecommerce.chat.service.OrderStatusService;
import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.enumeration.OrderStatus;
import com.ecommerce.library.kafka.event.order.CreateListOrderStatusEvent;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.chat.entity.Notification;
import com.ecommerce.chat.entity.NotificationType;
import com.ecommerce.chat.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderStatusServiceImpl implements OrderStatusService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationRepository notificationRepository;
    private final MessageService messageService;

    @Override
    public void sendOrderStatusMessage(CreateListOrderStatusEvent createListOrderStatusEvent) {
        if (createListOrderStatusEvent == null || createListOrderStatusEvent.getOrderStatusEventList().isEmpty()) {
            return;
        }

        boolean hasCancelled = createListOrderStatusEvent.getOrderStatusEventList().stream()
                .anyMatch(e -> e.getOrderStatus() == OrderStatus.CANCELLED);
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
}
