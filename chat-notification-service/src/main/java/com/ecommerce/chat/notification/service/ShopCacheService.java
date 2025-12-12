package com.ecommerce.chat.notification.service;

import com.ecommerce.library.kafka.event.shop.CreateShopEvent;
import com.ecommerce.library.kafka.event.shop.UpdateShopStatusEvent;

public interface ShopCacheService {
    void createShopCache(CreateShopEvent createShopEvent);

    void updateShopCacheStatus(UpdateShopStatusEvent updateShopStatusEvent);
}

