package com.ecommerce.chat.notification.service;

import com.ecommerce.library.kafka.event.order.CreateListOrderStatusEvent;
import com.ecommerce.library.kafka.event.order.OrderStatusEvent;
import com.ecommerce.library.kafka.event.payment.CreatePaymentEvent;


public interface OrderService {
    void sendOrderStatusMessage(CreateListOrderStatusEvent createListOrderStatusEvent);

    void sendPaymentNotificationMessage(CreatePaymentEvent createPaymentEvent);

    void sendOrderStatusUpdateNotification(OrderStatusEvent orderStatusEvent);
}
