package com.ecommerce.chat.service;

import com.ecommerce.chat.entity.ShopCache;
import com.ecommerce.library.kafka.event.shop.CreateShopEvent;
import com.ecommerce.library.kafka.event.shop.UpdateShopStatusEvent;

public interface ShopCacheService {
    void createShopCache(CreateShopEvent createShopEvent);

    void updateShopCacheStatus(UpdateShopStatusEvent updateShopStatusEvent);
}

