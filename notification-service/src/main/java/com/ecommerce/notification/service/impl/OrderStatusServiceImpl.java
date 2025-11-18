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

@Service
@RequiredArgsConstructor
public class OrderStatusServiceImpl implements OrderStatusService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationRepository notificationRepository;
    private final MessageService messageService;

    @Override
    public void sendOrderStatusMessage(OrderStatusEvent orderStatusEvent) {
        simpMessagingTemplate.convertAndSendToUser(
                String.valueOf(orderStatusEvent.getUserId()),
                "/queue/notify",
                orderStatusEvent
        );
        Notification notification = Notification.builder()
                .userId(orderStatusEvent.getUserId())
                .notificationType(NotificationType.ORDER_UPDATE)
                .isRead(false)
                .sentRealtime(true)
                .title(messageService.getMessage(orderStatusEvent.getOrderStatus() == OrderStatus.CANCELLED
                                        ? MessageSuccess.ORDER_CANCELLED_TITLE
                                        : MessageSuccess.ORDER_SUCCESS_TITLE)
                )
                .message(messageService.getMessage(orderStatusEvent.getOrderStatus() == OrderStatus.CANCELLED
                                        ? MessageSuccess.ORDER_CANCELLED_MESSAGE
                                        : MessageSuccess.ORDER_SUCCESS_MESSAGE)
                )
                .build();
        notificationRepository.save(notification);
    }
}
