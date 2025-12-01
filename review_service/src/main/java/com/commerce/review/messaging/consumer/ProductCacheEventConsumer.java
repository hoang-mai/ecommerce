package com.commerce.review.messaging.consumer;

import com.ecommerce.library.kafka.event.product.CreateProductCacheEvent;
import com.commerce.review.service.ProductCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.*;

@Service
@RequiredArgsConstructor
public class ProductCacheEventConsumer {

    private final ProductCacheService productCacheService;

    @KafkaListener(topics = CREATE_PRODUCT_CACHE_TOPIC, groupId = READ_SERVICE_GROUP)
    public void listenCreateProductCache(CreateProductCacheEvent createProductCacheEvent) {
        productCacheService.createProductCache(createProductCacheEvent);
    }
}

