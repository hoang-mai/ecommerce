package com.ecommerce.product.messaging.producer;

import com.ecommerce.library.kafka.event.product.CreateProductCacheEvent;
import com.ecommerce.library.kafka.event.product.CreateProductEvent;
import com.ecommerce.library.kafka.event.product.UpdateProductStatusEvent;
import com.ecommerce.library.kafka.event.product.UpdateProductVariantStatusEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.*;

@Service
@RequiredArgsConstructor
public class ProductEventProducer {
    private final KafkaTemplate<Long, CreateProductCacheEvent> createProductCacheEventKafkaTemplate;
    private final KafkaTemplate<Long, CreateProductEvent> createProductEventKafkaTemplate;
    private final KafkaTemplate<Long, UpdateProductVariantStatusEvent> updateProductVariantStatusEventKafkaTemplate;
    private final KafkaTemplate<Long, UpdateProductStatusEvent> updateProductStatusEventKafkaTemplate;

    public void send(CreateProductCacheEvent createProductCacheEvent) {
        createProductCacheEventKafkaTemplate.send(CREATE_PRODUCT_CACHE_TOPIC, createProductCacheEvent.getProductId(), createProductCacheEvent);
    }

    public void send(CreateProductEvent createProductEvent) {
        createProductEventKafkaTemplate.send(CREATE_PRODUCT_TOPIC, createProductEvent.getProductId(), createProductEvent);
    }

    public void send(UpdateProductVariantStatusEvent updateProductVariantStatusEvent){
        updateProductVariantStatusEventKafkaTemplate.send(UPDATE_VARIANT_STATUS_PRODUCT_TOPIC, updateProductVariantStatusEvent.getProductVariantId(), updateProductVariantStatusEvent);
    }

    public void send(UpdateProductStatusEvent updateProductStatusEvent){
        updateProductStatusEventKafkaTemplate.send(UPDATE_STATUS_PRODUCT_TOPIC, updateProductStatusEvent.getProductId(), updateProductStatusEvent);
    }
}
