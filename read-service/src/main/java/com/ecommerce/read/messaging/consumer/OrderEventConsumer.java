package com.ecommerce.read.messaging.consumer;

import com.ecommerce.library.kafka.event.order.CreateOrderEvent;
import com.ecommerce.library.kafka.event.order.OrderStatusEvent;
import com.ecommerce.read.service.OrderViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.*;

@Service
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final OrderViewService orderViewService;

    @KafkaListener(topics = CREATE_ORDER_VIEW_TOPIC, groupId = READ_SERVICE_GROUP)
    public void listen(CreateOrderEvent createOrderViewEvent){
        orderViewService.createOrderView(createOrderViewEvent);
    }

    @KafkaListener(topics =UPDATE_ORDER_STATUS_VIEW_TOPIC, groupId = READ_SERVICE_GROUP)
    public void listen(OrderStatusEvent orderStatusEvent){
        orderViewService.updateOrderStatusView(orderStatusEvent);
    }
}
