package com.ecommerce.notification.messaging.consumer;

import com.ecommerce.notification.service.OrderStatusService;
import com.ecommerce.library.kafka.event.order.OrderStatusEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.*;

@Service
@RequiredArgsConstructor
public class OrderStatusEventConsumer {

    private final OrderStatusService orderStatusService;

    @KafkaListener(topics = ORDER_STATUS_TOPIC, groupId = NOTIFICATION_SERVICE_GROUP)
    public void listenCreateUser(OrderStatusEvent orderStatusEvent) {
        orderStatusService.sendOrderStatusMessage(orderStatusEvent);
    }

}
