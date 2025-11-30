package com.ecommerce.product.messaging.producer;

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

    private final KafkaTemplate<Long, List<OrderStatusEvent>> orderStatusEventKafkaTemplate;
    private final KafkaTemplate<Long, List<CreateOrderEvent>> createOrderEventKafkaTemplate;

    public void sendStatus(List<OrderStatusEvent> orderStatusEventList){
        orderStatusEventKafkaTemplate.send(UPDATE_ORDER_STATUS_TOPIC, orderStatusEventList.get(0).getUserId(), orderStatusEventList);
        orderStatusEventKafkaTemplate.send(ORDER_STATUS_TOPIC, orderStatusEventList.get(0).getUserId(), orderStatusEventList);
    }

    public void send(List<CreateOrderEvent> createOrderEventList){
        createOrderEventKafkaTemplate.send(CREATE_ORDER_VIEW_TOPIC, createOrderEventList.get(0).getUserId(), createOrderEventList);
    }
}
