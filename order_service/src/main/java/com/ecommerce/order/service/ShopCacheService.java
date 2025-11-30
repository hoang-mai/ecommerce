package com.ecommerce.order.service;

import com.ecommerce.library.kafka.event.shop.CreateShopCacheEvent;

public interface ShopCacheService {
    void createShopCache(CreateShopCacheEvent createShopCacheEvent);
}
