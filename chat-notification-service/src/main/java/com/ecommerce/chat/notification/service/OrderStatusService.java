package com.ecommerce.chat.notification.service;

import com.ecommerce.library.kafka.event.order.CreateListOrderStatusEvent;


public interface OrderStatusService {
    void sendOrderStatusMessage(CreateListOrderStatusEvent createListOrderStatusEvent);
}
