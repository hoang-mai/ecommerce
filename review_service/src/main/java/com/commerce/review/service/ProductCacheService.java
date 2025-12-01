package com.commerce.review.service;

import com.ecommerce.library.kafka.event.product.CreateProductCacheEvent;

public interface ProductCacheService {
    void createProductCache(CreateProductCacheEvent createProductCacheEvent);
}

