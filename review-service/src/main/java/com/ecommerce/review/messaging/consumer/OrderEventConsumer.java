package com.ecommerce.review.messaging.consumer;

import com.ecommerce.library.kafka.event.order.CreateListOrderEvent;
import com.ecommerce.library.kafka.event.order.CreateOrderEvent;
import com.ecommerce.review.service.OrderItemCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.*;

@Service
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final OrderItemCacheService orderItemCacheService;

    @KafkaListener(topics = CREATE_ORDER_TOPIC, groupId = REVIEW_SERVICE_GROUP)
    public void listenCreateOrder(CreateListOrderEvent createListOrderEvent) {
        orderItemCacheService.createOrderItemCache(createListOrderEvent);
    }
}

