package com.ecommerce.chat.service;

import com.ecommerce.library.kafka.event.order.OderStatusEvent;

public interface OrderStatusService {
    void sendOrderStatusMessage(OderStatusEvent oderStatusEvent);
}
