package com.ecommerce.order.messaging.consumer;

import com.ecommerce.library.kafka.event.order.OderStatusEvent;
import com.ecommerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.ORDER_SERVICE_GROUP;
import static com.ecommerce.library.kafka.Constant.UPDATE_ORDER_STATUS_TOPIC;

@Service
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final OrderService orderService;
    @KafkaListener(topics = UPDATE_ORDER_STATUS_TOPIC, groupId = ORDER_SERVICE_GROUP)
    public void listenUpdateOrderStatus(OderStatusEvent oderStatusEvent) {
        orderService.updateOrderStatus(oderStatusEvent.getOrderId(), oderStatusEvent.getOrderStatus());
    }
}
