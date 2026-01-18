package com.ecommerce.product.messaging.producer;

import com.ecommerce.library.kafka.event.product.*;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.*;

@Service
@RequiredArgsConstructor
public class ProductEventProducer {
    private final KafkaTemplate<Long, CreateProductCacheEvent> createProductCacheEventKafkaTemplate;
    private final KafkaTemplate<Long, CreateProductEvent> createProductEventKafkaTemplate;
    private final KafkaTemplate<Long, UpdateProductStatusEvent> updateProductStatusEventKafkaTemplate;
    private final KafkaTemplate<Long, UpdateProductVariantStatusEvent> updateProductVariantStatusEventKafkaTemplate;
    private final KafkaTemplate<Long, UploadProductImageEvent> uploadProductImageEventKafkaTemplate;
    private final KafkaTemplate<Long, UpdateFlashSaleProductStockEvent> updateFlashSaleProductStockEventKafkaTemplate;

    public void send(CreateProductCacheEvent createProductCacheEvent) {
        createProductCacheEventKafkaTemplate.send(CREATE_PRODUCT_CACHE_TOPIC, createProductCacheEvent.getProductId(),
                createProductCacheEvent);
    }

    public void send(CreateProductEvent createProductEvent) {
        createProductEventKafkaTemplate.send(CREATE_PRODUCT_TOPIC, createProductEvent.getProductId(),
                createProductEvent);
    }

    public void send(UpdateProductStatusEvent updateProductStatusEvent) {
        updateProductStatusEventKafkaTemplate.send(UPDATE_STATUS_PRODUCT_TOPIC, updateProductStatusEvent.getProductId(),
                updateProductStatusEvent);
    }

    public void send(UpdateProductVariantStatusEvent updateProductVariantStatusEvent) {
        updateProductVariantStatusEventKafkaTemplate.send(UPDATE_STATUS_PRODUCT_VARIANT_TOPIC,
                updateProductVariantStatusEvent.getProductVariantId(), updateProductVariantStatusEvent);
    }

    public void send(UploadProductImageEvent uploadProductImageEvent) {
        uploadProductImageEventKafkaTemplate.send(UPLOAD_PRODUCT_IMAGE_TOPIC, uploadProductImageEvent.getProductId(),
                uploadProductImageEvent);
    }

    public void send(UpdateFlashSaleProductStockEvent updateFlashSaleProductStockEvent) {
        updateFlashSaleProductStockEventKafkaTemplate.send(UPDATE_FLASH_SALE_PRODUCT_STOCK_TOPIC,
                updateFlashSaleProductStockEvent.getFlashSaleProductId(), updateFlashSaleProductStockEvent);
    }

}
