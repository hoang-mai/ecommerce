package com.ecommerce.order.service;

import com.ecommerce.library.kafka.event.product.CreateProductCacheEvent;

public interface ProductCacheService {
    void createProductCache(CreateProductCacheEvent createProductCacheEvent);
}
