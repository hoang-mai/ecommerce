package com.commerce.review.messaging.consumer;

import com.ecommerce.library.kafka.event.order.CreateOrderEvent;
import com.commerce.review.service.OrderItemCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.*;

@Service
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final OrderItemCacheService orderItemCacheService;

    @KafkaListener(topics = CREATE_ORDER_TOPIC, groupId = READ_SERVICE_GROUP)
    public void listenCreateOrder(CreateOrderEvent createOrderEvent) {
        orderItemCacheService.createOrderItemCache(createOrderEvent);
    }
}

