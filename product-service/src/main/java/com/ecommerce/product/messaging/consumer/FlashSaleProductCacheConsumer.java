package com.ecommerce.product.messaging.consumer;

import com.ecommerce.library.kafka.event.flash.sale.FlashSaleProductEvent;
import com.ecommerce.product.service.FlashSaleProductCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.*;

@Service
@RequiredArgsConstructor
public class FlashSaleProductCacheConsumer {

    private final FlashSaleProductCacheService flashSaleProductCacheService;

    @KafkaListener(topics = CREATE_FLASH_SALE_PRODUCT_TOPIC, groupId = PRODUCT_SERVICE_GROUP)
    public void listenCreate(FlashSaleProductEvent flashSaleProductEvent) {
        flashSaleProductCacheService.createFlashSaleProductCache(flashSaleProductEvent);
    }

    @KafkaListener(topics = DELETE_FLASH_SALE_PRODUCT_TOPIC, groupId = PRODUCT_SERVICE_GROUP)
    public void listenDelete(FlashSaleProductEvent flashSaleProductEvent) {
        flashSaleProductCacheService.deleteFlashSaleProductCache(flashSaleProductEvent.getFlashSaleProductId());
    }
}
