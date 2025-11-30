package com.ecommerce.notification.service;

import com.ecommerce.library.kafka.event.order.OrderStatusEvent;

import java.util.List;

public interface OrderStatusService {
    void sendOrderStatusMessage(List<OrderStatusEvent> orderStatusEventList);
}
