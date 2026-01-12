package com.ecommerce.flash.sale.messaging.producer;

import com.ecommerce.library.kafka.event.flash.sale.FlashSaleProductEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.CREATE_FLASH_SALE_PRODUCT_TOPIC;
import static com.ecommerce.library.kafka.Constant.UPDATE_FLASH_SALE_PRODUCT_TOPIC;
import static com.ecommerce.library.kafka.Constant.DELETE_FLASH_SALE_PRODUCT_TOPIC;

@Service
@RequiredArgsConstructor
public class FlashSaleProductProducer {
    private final KafkaTemplate<Long, FlashSaleProductEvent> flashSaleProductEventKafkaTemplate;

    public void send(FlashSaleProductEvent event) {
        flashSaleProductEventKafkaTemplate.send(CREATE_FLASH_SALE_PRODUCT_TOPIC, event.getFlashSaleProductId(), event);
    }

    public void sendUpdate(FlashSaleProductEvent event) {
        flashSaleProductEventKafkaTemplate.send(UPDATE_FLASH_SALE_PRODUCT_TOPIC, event.getFlashSaleProductId(), event);
    }

    public void sendDelete(Long flashSaleProductId) {
        FlashSaleProductEvent event = FlashSaleProductEvent.builder()
            .flashSaleProductId(flashSaleProductId)
            .build();
        flashSaleProductEventKafkaTemplate.send(DELETE_FLASH_SALE_PRODUCT_TOPIC, flashSaleProductId, event);
    }

}
