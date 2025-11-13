package com.ecommerce.order.service.impl;

import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.enumeration.OrderStatus;
import com.ecommerce.library.exception.HttpRequestException;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.order.CreateOrderEvent;
import com.ecommerce.library.kafka.event.order.CreateOrderItemEvent;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.order.dto.ResCreateOrderDTO;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.messaging.producer.OrderEventProducer;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserHelper userHelper;
    private final OrderEventProducer orderEventProducer;

    @Override
    public void createOrder(ResCreateOrderDTO request) {
        Long userId = userHelper.getCurrentUserId();
        BigDecimal totalPrice = request.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create order
        Order order = Order.builder()
                .userId(userId)
                .orderStatus(OrderStatus.PENDING)
                .totalPrice(totalPrice)
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .build();

        request.getItems().forEach(item -> {
            OrderItem orderItem = OrderItem.builder()
                    .productId(item.getProductId())
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .productVariantId(item.getProductVariantId())
                    .build();
            order.addOrderItem(orderItem);
        });
        orderRepository.save(order);
        orderEventProducer.send(
                CreateOrderEvent.builder()
                        .orderId(order.getOrderId())
                        .createOrderItemEventList(order.getItems().stream()
                                .map(item -> CreateOrderItemEvent.builder()
                                        .productId(item.getProductId())
                                        .productVariantId(item.getProductVariantId())
                                        .quantity(item.getQuantity())
                                        .price(item.getPrice())
                                        .build()).toList())
                        .build()
        );
    }


    @Override
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(MessageError.ORDER_NOT_FOUND));

        order.setOrderStatus(newStatus);
        orderRepository.save(order);


    }

    @Override
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(MessageError.ORDER_NOT_FOUND));

        if (order.getOrderStatus() == OrderStatus.DELIVERED || order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new HttpRequestException(MessageError.CANNOT_CANCEL_ORDER, 400 , LocalDateTime.now());
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order = orderRepository.save(order);

    }

}

