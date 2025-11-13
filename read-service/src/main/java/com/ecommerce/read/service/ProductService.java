package com.ecommerce.read.service;

import com.ecommerce.library.kafka.event.product.CreateProductEvent;

public interface ProductService {
    void createProduct(CreateProductEvent createProductEvent);
}
