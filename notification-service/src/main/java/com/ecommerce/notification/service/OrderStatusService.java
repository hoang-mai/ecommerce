package com.ecommerce.notification.service;

import com.ecommerce.library.kafka.event.order.OderStatusEvent;

public interface OrderStatusService {
    void sendOrderStatusMessage(OderStatusEvent oderStatusEvent);
}
