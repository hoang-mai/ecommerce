package com.ecommerce.read.messaging.consumer;


import com.ecommerce.library.kafka.event.product.CreateProductEvent;
import com.ecommerce.library.kafka.event.product.UpdateProductStatusEvent;
import com.ecommerce.library.kafka.event.product.UpdateProductVariantStatusEvent;
import com.ecommerce.read.service.ProductViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.*;
import static com.ecommerce.library.kafka.Constant.READ_SERVICE_GROUP;

@Service
@RequiredArgsConstructor
public class ProductEventConsumer {
    private final ProductViewService productViewService;

    @KafkaListener(topics = CREATE_PRODUCT_TOPIC, groupId = READ_SERVICE_GROUP)
    public void listen(CreateProductEvent createProductEvent){
        productViewService.createProductEvent(createProductEvent);
    }

    @KafkaListener(topics = UPDATE_STATUS_PRODUCT_TOPIC, groupId = READ_SERVICE_GROUP)
    public void listen(UpdateProductStatusEvent event) {
        productViewService.updateProductStatus(event);
    }

    @KafkaListener(topics = UPDATE_STATUS_PRODUCT_VARIANT_TOPIC, groupId = READ_SERVICE_GROUP)
    public void listen(UpdateProductVariantStatusEvent event) {
        productViewService.updateProductVariantStatus(event);
    }
}
