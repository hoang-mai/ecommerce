package com.ecommerce.product.messaging.consumer;

import com.ecommerce.library.kafka.event.order.CreateListOrderEvent;
import com.ecommerce.library.kafka.event.order.CreateListOrderStatusEvent;
import com.ecommerce.library.kafka.event.product.RestoreStockEvent;
import com.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.CREATE_ORDER_TOPIC;
import static com.ecommerce.library.kafka.Constant.PRODUCT_SERVICE_GROUP;
import static com.ecommerce.library.kafka.Constant.UPDATE_ORDER_STATUS_TOPIC;
import static com.ecommerce.library.kafka.Constant.RESTORE_STOCK_TOPIC;

@Service
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ProductService productService;

    @KafkaListener(topics = CREATE_ORDER_TOPIC, groupId = PRODUCT_SERVICE_GROUP)
    public void listen(CreateListOrderEvent createListOrderEvent){
        productService.handleCreateOrderEvent(createListOrderEvent);
    }

    @KafkaListener(topics = RESTORE_STOCK_TOPIC, groupId = PRODUCT_SERVICE_GROUP)
    public void listenRestoreStock(RestoreStockEvent restoreStockEvent){
        productService.handleRestoreStockEvent(restoreStockEvent);
    }
}
