package com.ecommerce.chat.service;

import com.ecommerce.library.kafka.event.order.CreateListOrderStatusEvent;


public interface OrderStatusService {
    void sendOrderStatusMessage(CreateListOrderStatusEvent createListOrderStatusEvent);
}
