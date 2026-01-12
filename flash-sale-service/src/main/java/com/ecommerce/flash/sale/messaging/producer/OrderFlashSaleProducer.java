package com.ecommerce.flash.sale.messaging.producer;

import com.ecommerce.library.kafka.event.flash.sale.FlashSaleOrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.CREATE_FLASH_SALE_ORDER_TOPIC;

@Service
@RequiredArgsConstructor
public class OrderFlashSaleProducer {

    private final KafkaTemplate<Long, FlashSaleOrderEvent> flashSaleOrderEventKafkaTemplate;

    public void send(FlashSaleOrderEvent event) {
        flashSaleOrderEventKafkaTemplate.send(CREATE_FLASH_SALE_ORDER_TOPIC, event.getUserId(), event);
    }
}
