package com.ecommerce.product.messaging.producer;

import com.ecommerce.library.kafka.event.product.CreateProductCacheEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.*;

@Service
@RequiredArgsConstructor
public class ProductEventProducer {
    private final KafkaTemplate<Long, CreateProductCacheEvent> createProductCacheEventKafkaTemplate;

    public void send(CreateProductCacheEvent createProductCacheEvent) {
        createProductCacheEventKafkaTemplate.send(CREATE_PRODUCT_CACHE_TOPIC, createProductCacheEvent.getProductId(), createProductCacheEvent);
    }
}
