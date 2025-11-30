package com.ecommerce.order.service.impl;

import com.ecommerce.library.kafka.event.shop.CreateShopCacheEvent;
import com.ecommerce.order.entity.ShopCache;
import com.ecommerce.order.repository.ShopCacheRepository;
import com.ecommerce.order.service.ShopCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopCacheServiceImpl implements ShopCacheService {

    private final ShopCacheRepository shopCacheRepository;
    @Override
    public void createShopCache(CreateShopCacheEvent createShopCacheEvent) {
        shopCacheRepository.save(
                ShopCache.builder()
                        .shopId(createShopCacheEvent.getShopId())
                        .build()
        );
    }
}
