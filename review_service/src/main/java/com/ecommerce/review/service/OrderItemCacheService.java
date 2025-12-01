package com.ecommerce.review.service;

import com.ecommerce.library.kafka.event.order.CreateOrderEvent;

public interface OrderItemCacheService {
    void createOrderItemCache(CreateOrderEvent createOrderEvent);
}

