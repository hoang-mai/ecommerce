package com.ecommerce.product.messaging.producer;

import com.ecommerce.library.kafka.event.order.CreateListOrderEvent;
import com.ecommerce.library.kafka.event.order.CreateListOrderStatusEvent;
import com.ecommerce.library.kafka.event.order.CreateOrderEvent;
import com.ecommerce.library.kafka.event.order.OrderStatusEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ecommerce.library.kafka.Constant.*;

@Service
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<Long, CreateListOrderStatusEvent> orderStatusEventKafkaTemplate;
    private final KafkaTemplate<Long, CreateListOrderEvent> createOrderEventKafkaTemplate;

    public void sendStatus(CreateListOrderStatusEvent createListOrderStatusEvent){
        orderStatusEventKafkaTemplate.send(UPDATE_ORDER_STATUS_TOPIC, createListOrderStatusEvent.getUserId(), createListOrderStatusEvent);
    }

    public void send(CreateListOrderEvent createListOrderEvent){
        createOrderEventKafkaTemplate.send(CREATE_ORDER_VIEW_TOPIC, createListOrderEvent.getUserId(), createListOrderEvent);
        createOrderEventKafkaTemplate.send(CREATE_PAYMENT_TOPIC, createListOrderEvent.getUserId(), createListOrderEvent);
    }
}
