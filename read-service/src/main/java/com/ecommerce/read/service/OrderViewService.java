package com.ecommerce.read.service;

import com.ecommerce.library.kafka.event.order.CreateOrderEvent;
import com.ecommerce.library.kafka.event.order.OrderStatusEvent;

public interface OrderViewService {
    void createOrderView(CreateOrderEvent createOrderViewEvent);

    void updateOrderStatusView(OrderStatusEvent orderStatusEvent);
}
