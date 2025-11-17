package com.ecommerce.notification.service;

import com.ecommerce.library.kafka.event.order.OrderStatusEvent;

public interface OrderStatusService {
    void sendOrderStatusMessage(OrderStatusEvent orderStatusEvent);
}
