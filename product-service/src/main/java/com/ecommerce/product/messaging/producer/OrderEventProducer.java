package com.ecommerce.product.messaging.producer;

import com.ecommerce.library.kafka.event.order.CreateOrderEvent;
import com.ecommerce.library.kafka.event.order.OrderStatusEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.*;

@Service
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<Long, OrderStatusEvent> orderStatusEventKafkaTemplate;
    private final KafkaTemplate<Long, CreateOrderEvent> createOrderEventKafkaTemplate;

    public void send(OrderStatusEvent updateOrderStatusEvent){
        orderStatusEventKafkaTemplate.send(UPDATE_ORDER_STATUS_TOPIC, updateOrderStatusEvent.getOrderId(), updateOrderStatusEvent);
        orderStatusEventKafkaTemplate.send(ORDER_STATUS_TOPIC, updateOrderStatusEvent.getOrderId(), updateOrderStatusEvent);
    }

    public void send(CreateOrderEvent createOrderEvent){
        createOrderEventKafkaTemplate.send(CREATE_ORDER_VIEW_TOPIC, createOrderEvent.getOrderId(), createOrderEvent);
    }
}
