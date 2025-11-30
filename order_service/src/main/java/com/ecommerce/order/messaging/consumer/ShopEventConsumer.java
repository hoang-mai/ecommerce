package com.ecommerce.order.messaging.consumer;

import com.ecommerce.library.kafka.event.shop.CreateShopCacheEvent;
import com.ecommerce.order.service.ShopCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.CREATE_SHOP_CACHE_TOPIC;
import static com.ecommerce.library.kafka.Constant.ORDER_SERVICE_GROUP;

@Service
@RequiredArgsConstructor
public class ShopEventConsumer {

    private final ShopCacheService shopCacheService;
    @KafkaListener(topics = CREATE_SHOP_CACHE_TOPIC, groupId = ORDER_SERVICE_GROUP)
    public void listen(CreateShopCacheEvent createShopCacheEvent){
        shopCacheService.createShopCache(createShopCacheEvent);
    }
}
