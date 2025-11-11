package com.ecommerce.order.service;

import com.ecommerce.library.enumeration.OrderStatus;
import com.ecommerce.order.dto.CreateOrderRequest;
import com.ecommerce.order.dto.OrderResponse;

import java.util.List;

public interface OrderService {

    /**
     * Create new order
     */
    OrderResponse createOrder(Long userId, CreateOrderRequest request);

    /**
     * Get order by ID
     */
    OrderResponse getOrderById(Long userId, Long orderId);

    /**
     * Get all orders by user ID
     */
    List<OrderResponse> getOrdersByUserId(Long userId);

    /**
     * Get orders by user ID and status
     */
    List<OrderResponse> getOrdersByUserIdAndStatus(Long userId, OrderStatus status);

    /**
     * Update order status
     */
    OrderResponse updateOrderStatus(Long userId, Long orderId, OrderStatus newStatus);

    /**
     * Cancel order
     */
    OrderResponse cancelOrder(Long userId, Long orderId);
}

