package com.ecommerce.chat.notification.messaging.consumer;

import com.ecommerce.library.kafka.event.order.CreateListOrderStatusEvent;
import com.ecommerce.library.kafka.event.order.OrderStatusEvent;
import com.ecommerce.chat.notification.service.OrderService;
import com.ecommerce.library.kafka.event.payment.CreatePaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.*;

@Service
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = ORDER_STATUS_TOPIC, groupId = CHAT_NOTIFICATION_SERVICE_GROUP)
    public void listen(CreateListOrderStatusEvent createListOrderStatusEvent) {
        orderService.sendOrderStatusMessage(createListOrderStatusEvent);
    }

    @KafkaListener(topics = CREATE_PAYMENT_NOTIFICATION_TOPIC, groupId = CHAT_NOTIFICATION_SERVICE_GROUP)
    public void listen(CreatePaymentEvent createPaymentEvent) {
        orderService.sendPaymentNotificationMessage(createPaymentEvent);
    }

    @KafkaListener(topics = UPDATE_ORDER_STATUS_VIEW_TOPIC, groupId = CHAT_NOTIFICATION_SERVICE_GROUP)
    public void listenOrderStatusUpdate(OrderStatusEvent orderStatusEvent) {
        orderService.sendOrderStatusUpdateNotification(orderStatusEvent);
    }
}
