package com.ecommerce.chat.messaging.consumer;

import com.ecommerce.library.kafka.event.order.CreateListOrderStatusEvent;
import com.ecommerce.chat.service.OrderStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.*;

@Service
@RequiredArgsConstructor
public class OrderStatusEventConsumer {

    private final OrderStatusService orderStatusService;

    @KafkaListener(topics = ORDER_STATUS_TOPIC, groupId = NOTIFICATION_SERVICE_GROUP)
    public void listen(CreateListOrderStatusEvent createListOrderStatusEvent) {
        orderStatusService.sendOrderStatusMessage(createListOrderStatusEvent);
    }

}
