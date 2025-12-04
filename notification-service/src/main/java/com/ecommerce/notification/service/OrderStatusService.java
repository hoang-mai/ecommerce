package com.ecommerce.notification.service;

import com.ecommerce.library.kafka.event.order.CreateListOrderStatusEvent;


public interface OrderStatusService {
    void sendOrderStatusMessage(CreateListOrderStatusEvent createListOrderStatusEvent);
}
