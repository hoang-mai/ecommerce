package com.ecommerce.read.service;

import com.ecommerce.library.kafka.event.product.CreateProductEvent;
import com.ecommerce.library.kafka.event.product.UpdateProductStatusEvent;
import com.ecommerce.library.kafka.event.product.UpdateProductVariantStatusEvent;

public interface ProductViewService {
    void createProductEvent(CreateProductEvent createProductEvent);
    void updateProductStatus(UpdateProductStatusEvent event);
    void updateProductVariantStatus(UpdateProductVariantStatusEvent event);
}
