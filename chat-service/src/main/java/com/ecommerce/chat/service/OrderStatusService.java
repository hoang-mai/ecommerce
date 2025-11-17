package com.ecommerce.chat.service;

import com.ecommerce.library.kafka.event.order.OrderStatusEvent;

public interface OrderStatusService {
    void sendOrderStatusMessage(OrderStatusEvent orderStatusEvent);
}
