package com.ecommerce.read.service.impl;

import com.ecommerce.library.kafka.event.product.CreateProductEvent;
import com.ecommerce.read.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    @Override
    public void createProduct(CreateProductEvent createProductEvent) {

    }
}
