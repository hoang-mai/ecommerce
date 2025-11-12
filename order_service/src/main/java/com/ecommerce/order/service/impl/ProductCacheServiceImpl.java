package com.ecommerce.order.service.impl;

import com.ecommerce.library.kafka.event.product.CreateProductCacheEvent;
import com.ecommerce.order.entity.ProductCache;
import com.ecommerce.order.repository.ProductCacheRepository;
import com.ecommerce.order.service.ProductCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductCacheServiceImpl implements ProductCacheService {

    private final ProductCacheRepository productCacheRepository;

    public void createProductCache(CreateProductCacheEvent createProductCacheEvent) {
        productCacheRepository.save(ProductCache.builder()
                .productId(createProductCacheEvent.getProductId())
                .productVariantIds(createProductCacheEvent.getProductVariantId())
                .build());
    }
}
