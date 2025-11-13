package com.ecommerce.product.messaging.consumer;

import com.ecommerce.library.kafka.event.order.CreateOrderEvent;
import com.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.CREATE_ORDER_TOPIC;
import static com.ecommerce.library.kafka.Constant.PRODUCT_SERVICE_GROUP;

@Service
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ProductService productService;

    @KafkaListener(topics = CREATE_ORDER_TOPIC, groupId = PRODUCT_SERVICE_GROUP)
    public void listen(CreateOrderEvent createOrderEvent){
        productService.handleCreateOrderEvent(createOrderEvent);
    }
}
