package com.ecommerce.chat.notification.messaging.consumer;

import com.ecommerce.chat.notification.service.ChatService;
import com.ecommerce.chat.notification.service.ShopCacheService;
import com.ecommerce.library.kafka.event.shop.CreateShopEvent;
import com.ecommerce.library.kafka.event.shop.UpdateShopStatusEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.*;

@Service
@RequiredArgsConstructor
public class ShopEventConsumer {

    private final ShopCacheService shopCacheService;
    private final ChatService chatService;

    @KafkaListener(topics = CREATE_SHOP_TOPIC, groupId = CHAT_NOTIFICATION_SERVICE_GROUP)
    public void listen(CreateShopEvent createShopEvent) {
            shopCacheService.createShopCache(createShopEvent);
            chatService.updateShopInChats(createShopEvent);
    }
    @KafkaListener(topics = UPDATE_SHOP_STATUS_TOPIC, groupId = CHAT_NOTIFICATION_SERVICE_GROUP)
    public void listen(UpdateShopStatusEvent updateShopStatusEvent){
        shopCacheService.updateShopCacheStatus(updateShopStatusEvent);
        chatService.updateShopStatusInChats(updateShopStatusEvent);
    }

}

