package com.ecommerce.notification.consumer;

import com.ecommerce.notification.service.OrderStatusService;
import com.ecommerce.library.kafka.event.order.OderStatusEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.CHAT_SERVICE_GROUP;
import static com.ecommerce.library.kafka.Constant.ORDER_STATUS_TOPIC;

@Service
@RequiredArgsConstructor
public class OrderStatusEventConsumer {

    private final OrderStatusService orderStatusService;

    @KafkaListener(topics = ORDER_STATUS_TOPIC, groupId = CHAT_SERVICE_GROUP)
    public void listenCreateUser(OderStatusEvent oderStatusEvent) {
        orderStatusService.sendOrderStatusMessage(oderStatusEvent);
    }

}
