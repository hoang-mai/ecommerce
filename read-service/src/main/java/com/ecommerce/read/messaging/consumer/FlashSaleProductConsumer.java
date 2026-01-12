package com.ecommerce.read.messaging.consumer;

import com.ecommerce.library.kafka.event.flash.sale.FlashSaleProductEvent;
import com.ecommerce.read.service.FlashSaleProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.CREATE_FLASH_SALE_PRODUCT_TOPIC;
import static com.ecommerce.library.kafka.Constant.UPDATE_FLASH_SALE_PRODUCT_TOPIC;
import static com.ecommerce.library.kafka.Constant.DELETE_FLASH_SALE_PRODUCT_TOPIC;
import static com.ecommerce.library.kafka.Constant.READ_SERVICE_GROUP;

@Service
@RequiredArgsConstructor
public class FlashSaleProductConsumer {
    private  final FlashSaleProductService flashSaleProductService;

    @KafkaListener(topics = CREATE_FLASH_SALE_PRODUCT_TOPIC, groupId = READ_SERVICE_GROUP)
    public void listenCreate(FlashSaleProductEvent event) {
        flashSaleProductService.createFlashSaleProduct(event);
    }

    @KafkaListener(topics = UPDATE_FLASH_SALE_PRODUCT_TOPIC, groupId = READ_SERVICE_GROUP)
    public void listenUpdate(FlashSaleProductEvent event) {
        flashSaleProductService.updateFlashSaleProduct(event);
    }

    @KafkaListener(topics = DELETE_FLASH_SALE_PRODUCT_TOPIC, groupId = READ_SERVICE_GROUP)
    public void listenDelete(FlashSaleProductEvent event) {
        flashSaleProductService.deleteFlashSaleProduct(event.getFlashSaleProductId());
    }
}
