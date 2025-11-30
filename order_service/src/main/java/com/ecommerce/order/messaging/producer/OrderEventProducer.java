package com.ecommerce.order.messaging.producer;

import com.ecommerce.library.kafka.event.order.CreateOrderEvent;
import com.ecommerce.library.kafka.event.order.OrderStatusEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ecommerce.library.kafka.Constant.CREATE_ORDER_TOPIC;
import static com.ecommerce.library.kafka.Constant.UPDATE_ORDER_STATUS_VIEW_TOPIC;

@Service
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<Long , List<CreateOrderEvent>> kafkaTemplate;
    private final KafkaTemplate<Long, OrderStatusEvent> statusEventKafkaTemplate;

    public void send(List<CreateOrderEvent> createOrderEventList, Long userId){
        kafkaTemplate.send(CREATE_ORDER_TOPIC, userId, createOrderEventList);
    }

    public void send(OrderStatusEvent orderStatusEvent){
        statusEventKafkaTemplate.send(UPDATE_ORDER_STATUS_VIEW_TOPIC, orderStatusEvent.getOrderId(), orderStatusEvent);
    }


}
