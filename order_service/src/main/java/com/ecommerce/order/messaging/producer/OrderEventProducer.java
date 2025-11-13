package com.ecommerce.order.messaging.producer;

import com.ecommerce.library.kafka.event.order.CreateOrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.CREATE_ORDER_TOPIC;

@Service
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<Long , CreateOrderEvent> kafkaTemplate;

    public void send(CreateOrderEvent createOrderEvent){
        kafkaTemplate.send(CREATE_ORDER_TOPIC, createOrderEvent.getOrderId(), createOrderEvent);
    }
}
