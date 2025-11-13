package com.ecommerce.product.messaging.producer;

import com.ecommerce.library.kafka.event.order.UpdateOrderStatusEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.UPDATE_ORDER_STATUS_TOPIC;

@Service
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<Long, UpdateOrderStatusEvent> kafkaTemplate;

    public void send(UpdateOrderStatusEvent updateOrderStatusEvent){
        kafkaTemplate.send(UPDATE_ORDER_STATUS_TOPIC, updateOrderStatusEvent.getOrderId(), updateOrderStatusEvent);
    }
}
