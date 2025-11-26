package com.ecommerce.product.messaging.producer;

import com.ecommerce.library.kafka.event.shop.CreateShopEvent;
import com.ecommerce.library.kafka.event.shop.UpdateShopStatusEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.CREATE_SHOP_TOPIC;
import static com.ecommerce.library.kafka.Constant.UPDATE_SHOP_STATUS_TOPIC;

@Service
@RequiredArgsConstructor
public class ShopEventProducer {
    private final KafkaTemplate<Long, UpdateShopStatusEvent> updateShopStatusEventKafkaTemplate;
    private final KafkaTemplate<Long, CreateShopEvent> createShopEventKafkaTemplate;

    public void send(UpdateShopStatusEvent updateShopStatusEvent) {
        updateShopStatusEventKafkaTemplate.send(UPDATE_SHOP_STATUS_TOPIC, updateShopStatusEvent.getShopId(), updateShopStatusEvent);
    }

    public void send(CreateShopEvent createShopEvent) {
        createShopEventKafkaTemplate.send(CREATE_SHOP_TOPIC, createShopEvent.getShopId(), createShopEvent);
    }

}
