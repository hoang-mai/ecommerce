package com.ecommerce.order.service.impl;

import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.enumeration.OrderStatus;
import com.ecommerce.library.exception.HttpRequestException;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.order.CreateOrderEvent;
import com.ecommerce.library.kafka.event.order.CreateOrderItemEvent;
import com.ecommerce.library.kafka.event.order.CreateProductOrderItemEvent;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.order.dto.ReqUpdateOrderStatus;
import com.ecommerce.order.dto.ResCreateOrderDTO;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.entity.ProductOrderItem;
import com.ecommerce.order.messaging.producer.OrderEventProducer;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

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
                .map(item -> item.getProductOrderItems().stream().map(
                        productItem -> productItem.getPrice().multiply(BigDecimal.valueOf(productItem.getQuantity()))
                ).reduce(BigDecimal.ZERO, BigDecimal::add)).reduce(BigDecimal.ZERO, BigDecimal::add);

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
                    .build();
            item.getProductOrderItems().forEach(productItem -> {
                ProductOrderItem productOrderItem = ProductOrderItem.builder()
                        .productVariantId(productItem.getProductVariantId())
                        .quantity(productItem.getQuantity())
                        .price(productItem.getPrice())
                        .build();
                orderItem.addProductOrderItem(productOrderItem);
            });
            order.addOrderItem(orderItem);
        });
        orderRepository.save(order);
        orderEventProducer.send(
                CreateOrderEvent.builder()
                        .orderId(order.getOrderId())
                        .createOrderItemEventList(order.getItems().stream()
                                .map(item -> CreateOrderItemEvent.builder()
                                        .orderItemId(item.getOrderItemId())
                                        .productId(item.getProductId())
                                        .createProductOrderItemEvents(item.getProductOrderItems().stream()
                                                .map(productItem -> CreateProductOrderItemEvent.builder()
                                                        .productVariantId(productItem.getProductVariantId())
                                                        .quantity(productItem.getQuantity())
                                                        .price(productItem.getPrice())
                                                        .build()).toList())
                                        .build()).toList())
                        .build()
        );
    }


    @Override
    public void updateOrderStatus(Long orderId, ReqUpdateOrderStatus reqUpdateOrderStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(MessageError.ORDER_NOT_FOUND));

        OrderStatus currentStatus = order.getOrderStatus();
        OrderStatus newStatus = reqUpdateOrderStatus.getOrderStatus();
        if (!transitions.getOrDefault(currentStatus, Set.of()).contains(newStatus)) {
            throw new HttpRequestException(MessageError.INVALID_ORDER_STATUS_TRANSITION, 400, LocalDateTime.now());
        }
        order.setOrderStatus(newStatus);

        if (newStatus == OrderStatus.CANCELLED || newStatus == OrderStatus.RETURNED) {
            order.setReason(reqUpdateOrderStatus.getReason());
        }

        orderRepository.save(order);


    }

    private static final Map<OrderStatus, Set<OrderStatus>> transitions = Map.of(
            OrderStatus.PENDING, Set.of(OrderStatus.PAID, OrderStatus.CANCELLED),
            OrderStatus.PAID, Set.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
            OrderStatus.CONFIRMED, Set.of(OrderStatus.SHIPPED),
            OrderStatus.SHIPPED, Set.of(OrderStatus.DELIVERED),
            OrderStatus.DELIVERED, Set.of(OrderStatus.COMPLETED, OrderStatus.RETURNED),
            OrderStatus.COMPLETED, Set.of(OrderStatus.RETURNED)
    );

}

