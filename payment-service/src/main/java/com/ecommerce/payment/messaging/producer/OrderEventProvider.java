package com.ecommerce.payment.messaging.producer;

import com.ecommerce.library.kafka.event.order.CreateListOrderStatusEvent;
import com.ecommerce.library.kafka.event.payment.CreatePaymentEvent;
import com.ecommerce.library.kafka.event.product.RestoreStockEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.*;

@Service
@RequiredArgsConstructor
public class OrderEventProvider {

    private final KafkaTemplate<Long , CreatePaymentEvent> createPaymentEventKafkaListener;
    private final KafkaTemplate<Long, CreateListOrderStatusEvent> orderStatusEventKafkaTemplate;
    private final KafkaTemplate<Long, RestoreStockEvent> restoreStockEventKafkaTemplate;

    public void send(CreatePaymentEvent createPaymentEvent){
        createPaymentEventKafkaListener.send(CREATE_PAYMENT_NOTIFICATION_TOPIC, createPaymentEvent.getUserId(), createPaymentEvent);
    }

    public void sendUpdatePaymentStatusEvent(CreateListOrderStatusEvent createListOrderStatusEvent) {
        orderStatusEventKafkaTemplate.send(ORDER_STATUS_TOPIC, createListOrderStatusEvent.getUserId(), createListOrderStatusEvent);
        orderStatusEventKafkaTemplate.send(UPDATE_ORDER_STATUS_TOPIC, createListOrderStatusEvent.getUserId(), createListOrderStatusEvent);
    }

    public void sendRestoreStockEvent(RestoreStockEvent restoreStockEvent) {
        restoreStockEventKafkaTemplate.send(RESTORE_STOCK_TOPIC, restoreStockEvent.getUserId(), restoreStockEvent);
    }
}
