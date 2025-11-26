package com.ecommerce.read.messaging.consumer;

import com.ecommerce.library.kafka.event.shop.CreateShopEvent;
import com.ecommerce.library.kafka.event.shop.UpdateShopStatusEvent;
import com.ecommerce.read.service.ShopViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.*;

@Service
@RequiredArgsConstructor
public class ShopEventConsumer {

    private final ShopViewService shopViewService;

    @KafkaListener(topics = CREATE_SHOP_TOPIC, groupId = READ_SERVICE_GROUP)
    public void listen(CreateShopEvent createShopEvent){
        shopViewService.createShopView(createShopEvent);
    }

    @KafkaListener(topics = UPDATE_SHOP_STATUS_TOPIC, groupId = READ_SERVICE_GROUP)
    public void listen(UpdateShopStatusEvent updateShopStatusEvent){
        shopViewService.updateShopStatusView(updateShopStatusEvent);
    }
}
