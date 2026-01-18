package com.ecommerce.flash.sale.messaging.producer;

import com.ecommerce.library.kafka.event.order.CreateListOrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.CREATE_ORDER_TOPIC;


@Service
@RequiredArgsConstructor
public class OrderFlashSaleProducer {
    private final KafkaTemplate<Long , CreateListOrderEvent> kafkaTemplate;

    public void send(CreateListOrderEvent createListOrderEvent){
        kafkaTemplate.send(CREATE_ORDER_TOPIC, createListOrderEvent.getUserId(), createListOrderEvent);
    }
}
