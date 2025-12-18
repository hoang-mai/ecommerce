package com.ecommerce.payment.messaging.consumer;

import com.ecommerce.library.kafka.event.order.CreateListOrderEvent;
import com.ecommerce.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.CREATE_PAYMENT_TOPIC;
import static com.ecommerce.library.kafka.Constant.PAYMENT_SERVICE_GROUP;

@Service
@RequiredArgsConstructor
public class OrderEventConsumer {
    private final PaymentService paymentService;

    @KafkaListener(topics = CREATE_PAYMENT_TOPIC, groupId = PAYMENT_SERVICE_GROUP)
    public void listen(CreateListOrderEvent createListOrderEvent) {
        paymentService.handleCreatePaymentEvent(createListOrderEvent);
    }
}
