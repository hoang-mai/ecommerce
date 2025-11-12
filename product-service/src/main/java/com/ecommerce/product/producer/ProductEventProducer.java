package com.ecommerce.product.producer;

import com.ecommerce.library.kafka.event.product.CreateProductCacheEvent;
import com.ecommerce.library.kafka.event.product.CreateProductEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.CREATE_PRODUCT_CACHE_TOPIC;
import static com.ecommerce.library.kafka.Constant.CREATE_PRODUCT_TOPIC;

@Service
@RequiredArgsConstructor
public class ProductEventProducer {
    private final KafkaTemplate<Long, CreateProductCacheEvent> createProductCacheEventKafkaTemplate;
    private final KafkaTemplate<Long, CreateProductEvent> kafkaUpdateAvatarUserTemplate;

    public void send(CreateProductCacheEvent createProductCacheEvent) {
        createProductCacheEventKafkaTemplate.send(CREATE_PRODUCT_CACHE_TOPIC, createProductCacheEvent.getProductId(), createProductCacheEvent);
    }

    public void send(CreateProductEvent createProductEvent) {
        kafkaUpdateAvatarUserTemplate.send(CREATE_PRODUCT_TOPIC, createProductEvent.getProductId(), createProductEvent);
    }
}
