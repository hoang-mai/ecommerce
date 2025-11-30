package com.ecommerce.order.service;

import com.ecommerce.library.kafka.event.order.OrderStatusEvent;
import com.ecommerce.order.dto.ReqUpdateOrderStatus;
import com.ecommerce.order.dto.ResCreateOrderDTO;

import java.util.List;

public interface OrderService {

    /**
     * Create new order
     */
    void createOrder(ResCreateOrderDTO request);

    /**
     * Update order status
     */
    void updateOrderStatus(Long orderId, ReqUpdateOrderStatus reqUpdateOrderStatus);

    /**
     * Update order status
     */
    void updateOrderStatus(List<OrderStatusEvent> orderStatusEventList);

}

