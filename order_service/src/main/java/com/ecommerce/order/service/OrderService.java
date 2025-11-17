package com.ecommerce.order.service;

import com.ecommerce.library.enumeration.OrderStatus;
import com.ecommerce.order.dto.ReqUpdateOrderStatus;
import com.ecommerce.order.dto.ResCreateOrderDTO;

public interface OrderService {

    /**
     * Create new order
     */
    void createOrder(ResCreateOrderDTO request);

    /**
     * Update order status
     */
    void updateOrderStatus(Long orderId, ReqUpdateOrderStatus reqUpdateOrderStatus);

}

