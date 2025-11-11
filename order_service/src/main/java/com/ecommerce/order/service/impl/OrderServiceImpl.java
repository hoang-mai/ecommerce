package com.ecommerce.order.service.impl;

import com.ecommerce.library.enumeration.OrderStatus;
import com.ecommerce.order.dto.CreateOrderRequest;
import com.ecommerce.order.dto.OrderItemRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.mapper.OrderMapper;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {
        // Calculate total price
        BigDecimal totalPrice = request.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create order
        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.PENDING)
                .totalPrice(totalPrice)
                .shippingAddressId(request.getShippingAddressId())
                .items(new ArrayList<>())
                .build();

        // Create order items
        List<OrderItem> orderItems = request.getItems().stream()
                .map(itemRequest -> createOrderItem(order, itemRequest))
                .collect(Collectors.toList());

        order.setItems(orderItems);
        order = orderRepository.save(order);

        log.info("Created order: {} for user: {}", order.getOrderId(), userId);

        return orderMapper.toOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findByOrderIdAndUserId(orderId, userId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        return orderMapper.toOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(orderMapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserIdAndStatus(Long userId, OrderStatus status) {
        List<Order> orders = orderRepository.findByUserIdAndStatus(userId, status);
        return orders.stream()
                .map(orderMapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse updateOrderStatus(Long userId, Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findByOrderIdAndUserId(orderId, userId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        order.setStatus(newStatus);
        order = orderRepository.save(order);

        log.info("Updated order status: {} to {} for user: {}", orderId, newStatus, userId);

        return orderMapper.toOrderResponse(order);
    }

    @Override
    public OrderResponse cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByOrderIdAndUserId(orderId, userId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Cannot cancel order with status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        order = orderRepository.save(order);

        log.info("Cancelled order: {} for user: {}", orderId, userId);

        return orderMapper.toOrderResponse(order);
    }

    private OrderItem createOrderItem(Order order, OrderItemRequest itemRequest) {
        return OrderItem.builder()
                .order(order)
                .productId(itemRequest.getProductId())
                .quantity(itemRequest.getQuantity())
                .price(itemRequest.getPrice())
                .build();
    }
}

