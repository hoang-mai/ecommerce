package com.commerce.review.service.impl;

import com.ecommerce.library.kafka.event.product.CreateProductCacheEvent;
import com.commerce.review.entity.ProductCache;
import com.commerce.review.repository.ProductCacheRepository;
import com.commerce.review.service.ProductCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductCacheServiceImpl implements ProductCacheService {

    private final ProductCacheRepository productCacheRepository;

    @Override
    public void createProductCache(CreateProductCacheEvent createProductCacheEvent) {
        productCacheRepository.save(ProductCache.builder()
                .productId(createProductCacheEvent.getProductId())
                .productVariantIds(createProductCacheEvent.getProductVariantId())
                .build());
    }
}

