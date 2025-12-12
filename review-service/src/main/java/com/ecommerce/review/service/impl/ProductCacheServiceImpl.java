package com.ecommerce.review.service.impl;

import com.ecommerce.library.kafka.event.product.CreateProductCacheEvent;
import com.ecommerce.review.entity.ProductCache;
import com.ecommerce.review.repository.ProductCacheRepository;
import com.ecommerce.review.service.ProductCacheService;
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

