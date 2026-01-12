package com.ecommerce.order.messaging.consumer;

import com.ecommerce.library.kafka.event.flash.sale.FlashSaleOrderEvent;
import com.ecommerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.CREATE_FLASH_SALE_ORDER_TOPIC;
import static com.ecommerce.library.kafka.Constant.ORDER_SERVICE_GROUP;

@Service
@RequiredArgsConstructor
public class FlashSaleOrderEventConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = CREATE_FLASH_SALE_ORDER_TOPIC, groupId = ORDER_SERVICE_GROUP)
    public void listen(FlashSaleOrderEvent flashSaleOrderEvent) {
        orderService.createFlashSaleOrder(flashSaleOrderEvent);
    }
}

