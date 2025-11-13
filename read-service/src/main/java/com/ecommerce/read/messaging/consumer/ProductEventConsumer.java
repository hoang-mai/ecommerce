package com.ecommerce.read.messaging.consumer;

import com.ecommerce.library.kafka.event.product.CreateProductEvent;
import com.ecommerce.read.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.CREATE_PRODUCT_TOPIC;
import static com.ecommerce.library.kafka.Constant.READ_SERVICE_GROUP;

@Service
@RequiredArgsConstructor
public class ProductEventConsumer {

    private final ProductService productService;

    @KafkaListener(topics =CREATE_PRODUCT_TOPIC, groupId = READ_SERVICE_GROUP)
    public void listen(CreateProductEvent createProductEvent){
        productService.createProduct(createProductEvent);
    }
}
