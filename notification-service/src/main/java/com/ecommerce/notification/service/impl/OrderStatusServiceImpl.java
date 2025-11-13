package com.ecommerce.notification.service.impl;

import com.ecommerce.library.kafka.event.order.OderStatusEvent;
import com.ecommerce.notification.service.OrderStatusService;

public class OrderStatusServiceImpl implements OrderStatusService {
    @Override
    public void sendOrderStatusMessage(OderStatusEvent oderStatusEvent) {

    }
}
