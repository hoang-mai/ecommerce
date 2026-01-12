package com.ecommerce.order.service.impl;

import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.enumeration.OrderStatus;
import com.ecommerce.library.exception.HttpRequestException;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.flash.sale.FlashSaleOrderEvent;
import com.ecommerce.library.kafka.event.order.*;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.order.dto.ReqUpdateOrderStatus;
import com.ecommerce.order.dto.ResCreateOrderDTO;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.entity.ProductCache;
import com.ecommerce.order.messaging.producer.OrderEventProducer;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.repository.ProductCacheRepository;
import com.ecommerce.order.repository.ShopCacheRepository;
import com.ecommerce.order.service.CartService;
import com.ecommerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductCacheRepository productCacheRepository;
    private final ShopCacheRepository shopCacheRepository;
    private final UserHelper userHelper;
    private final OrderEventProducer orderEventProducer;
    private final CartService cartService;

    @Transactional
    @Override
    public void createOrder(ResCreateOrderDTO request) {
        Long userId = userHelper.getCurrentUserId();
        List<Order> orders = new ArrayList<>();
        List<Long> cartItemIds = new ArrayList<>();

        request.getItems().forEach(item -> {
            if (!shopCacheRepository.existsById(item.getShopId())) {
                throw new NotFoundException(MessageError.SHOP_NOT_FOUND);
            }
            cartItemIds.add(item.getCartItemId());
            Order order = Order.builder()
                .userId(userId)
                .shopId(item.getShopId())
                .orderStatus(OrderStatus.PENDING)
                .receiverName(request.getReceiverName())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .cartItemId(item.getCartItemId())
                .note(item.getNote())
                .build();
            item.getProductOrderItems().forEach(productOrderItem -> {

                ProductCache productCache = productCacheRepository.findById(productOrderItem.getProductId())
                    .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));

                if (!productCache.getProductVariantIds().contains(productOrderItem.getProductVariantId())) {
                    throw new NotFoundException(MessageError.PRODUCT_VARIANT_NOT_FOUND);
                }

                BigDecimal totalPrice = productOrderItem.getPrice().multiply(BigDecimal.valueOf(productOrderItem.getQuantity()));
                BigDecimal totalDiscount = totalPrice.multiply(
                    BigDecimal.valueOf(productOrderItem.getDiscount())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                );
                BigDecimal totalFinalPrice = totalPrice.subtract(totalDiscount);
                OrderItem orderItem = OrderItem.builder()
                    .productId(productOrderItem.getProductId())
                    .productVariantId(productOrderItem.getProductVariantId())
                    .totalPrice(totalPrice)
                    .totalDiscount(totalDiscount)
                    .totalFinalPrice(totalFinalPrice)
                    .price(productOrderItem.getPrice())
                    .quantity(productOrderItem.getQuantity())
                    .isFlashSale(productOrderItem.getIsFlashSale())
                    .build();
                order.addTotalPrice(orderItem.getTotalFinalPrice());
                order.addOrderItem(orderItem);
            });
            orders.add(order);
        });
        orderRepository.saveAll(orders);
        cartService.clearCartItems(cartItemIds);

        orderEventProducer.send(
            CreateListOrderEvent.builder()
                .userId(userId)
                .createOrderEventList(orders.stream().map(order -> CreateOrderEvent.builder()
                    .orderId(order.getOrderId())
                    .userId(order.getUserId())
                    .shopId(order.getShopId())
                    .orderStatus(order.getOrderStatus())
                    .totalPrice(order.getTotalPrice())
                    .receiverName(order.getReceiverName())
                    .address(order.getAddress())
                    .phoneNumber(order.getPhoneNumber())
                    .createdAt(order.getCreatedAt())
                    .updatedAt(order.getUpdatedAt())
                    .cartItemId(order.getCartItemId())
                    .note(order.getNote())
                    .createOrderItemEventList(
                        order.getItems().stream().map(orderItem -> CreateOrderItemEvent.builder()
                            .orderItemId(orderItem.getOrderItemId())
                            .productId(orderItem.getProductId())
                            .productVariantId(orderItem.getProductVariantId())
                            .quantity(orderItem.getQuantity())
                            .price(orderItem.getPrice())
                            .totalPrice(orderItem.getTotalPrice())
                            .totalDiscount(orderItem.getTotalDiscount())
                            .totalFinalPrice(orderItem.getTotalFinalPrice())
                            .isFlashSale(orderItem.getIsFlashSale())
                            .build()
                        ).toList()
                    )
                    .build()).toList())
                .build()
        );
    }

    @Transactional
    @Override
    public void createFlashSaleOrder(FlashSaleOrderEvent flashSaleOrderEvent) {
        Long userId = flashSaleOrderEvent.getUserId();
        List<Order> orders = new ArrayList<>();
        List<Long> cartItemIds = new ArrayList<>();

        flashSaleOrderEvent.getItems().forEach(item -> {
            if (!shopCacheRepository.existsById(item.getShopId())) {
                throw new NotFoundException(MessageError.SHOP_NOT_FOUND);
            }
            cartItemIds.add(item.getCartItemId());
            Order order = Order.builder()
                .userId(userId)
                .shopId(item.getShopId())
                .orderStatus(OrderStatus.PENDING)
                .receiverName(flashSaleOrderEvent.getReceiverName())
                .address(flashSaleOrderEvent.getAddress())
                .phoneNumber(flashSaleOrderEvent.getPhoneNumber())
                .cartItemId(item.getCartItemId())
                .note(item.getNote())
                .build();
            item.getProductOrderItems().forEach(productOrderItem -> {

                ProductCache productCache = productCacheRepository.findById(productOrderItem.getProductId())
                    .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));

                if (!productCache.getProductVariantIds().contains(productOrderItem.getProductVariantId())) {
                    throw new NotFoundException(MessageError.PRODUCT_VARIANT_NOT_FOUND);
                }

                BigDecimal totalPrice = productOrderItem.getPrice().multiply(BigDecimal.valueOf(productOrderItem.getQuantity()));
                BigDecimal totalDiscount = totalPrice.multiply(
                    BigDecimal.valueOf(productOrderItem.getDiscount())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                );
                BigDecimal totalFinalPrice = totalPrice.subtract(totalDiscount);
                OrderItem orderItem = OrderItem.builder()
                    .productId(productOrderItem.getProductId())
                    .productVariantId(productOrderItem.getProductVariantId())
                    .totalPrice(totalPrice)
                    .totalDiscount(totalDiscount)
                    .totalFinalPrice(totalFinalPrice)
                    .price(productOrderItem.getPrice())
                    .quantity(productOrderItem.getQuantity())
                    .isFlashSale(productOrderItem.getIsFlashSale())
                    .build();
                order.addTotalPrice(orderItem.getTotalFinalPrice());
                order.addOrderItem(orderItem);
            });
            orders.add(order);
        });
        orderRepository.saveAll(orders);
        cartService.clearCartItems(cartItemIds);

        orderEventProducer.send(
            CreateListOrderEvent.builder()
                .userId(userId)
                .createOrderEventList(orders.stream().map(order -> CreateOrderEvent.builder()
                    .orderId(order.getOrderId())
                    .userId(order.getUserId())
                    .shopId(order.getShopId())
                    .orderStatus(order.getOrderStatus())
                    .totalPrice(order.getTotalPrice())
                    .receiverName(order.getReceiverName())
                    .address(order.getAddress())
                    .phoneNumber(order.getPhoneNumber())
                    .createdAt(order.getCreatedAt())
                    .updatedAt(order.getUpdatedAt())
                    .cartItemId(order.getCartItemId())
                    .note(order.getNote())
                    .createOrderItemEventList(
                        order.getItems().stream().map(orderItem -> CreateOrderItemEvent.builder()
                            .orderItemId(orderItem.getOrderItemId())
                            .productId(orderItem.getProductId())
                            .productVariantId(orderItem.getProductVariantId())
                            .quantity(orderItem.getQuantity())
                            .price(orderItem.getPrice())
                            .totalPrice(orderItem.getTotalPrice())
                            .totalDiscount(orderItem.getTotalDiscount())
                            .totalFinalPrice(orderItem.getTotalFinalPrice())
                            .isFlashSale(orderItem.getIsFlashSale())
                            .build()
                        ).toList()
                    )
                    .build()).toList())
                .build()
        );
    }


    @Override
    public void updateOrderStatus(Long orderId, ReqUpdateOrderStatus reqUpdateOrderStatus) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new NotFoundException(MessageError.ORDER_ITEM_NOT_FOUND));

        OrderStatus currentStatus = order.getOrderStatus();
        OrderStatus newStatus = reqUpdateOrderStatus.getOrderStatus();
        if (!transitions.getOrDefault(currentStatus, Set.of()).contains(newStatus)) {
            throw new HttpRequestException(MessageError.INVALID_ORDER_STATUS_TRANSITION, 400, Instant.now());
        }
        order.setOrderStatus(newStatus);

        if (newStatus == OrderStatus.CANCELLED || newStatus == OrderStatus.RETURNED) {
            order.setReason(reqUpdateOrderStatus.getReason());
        }

        orderRepository.save(order);

        orderEventProducer.send(
            OrderStatusEvent.builder()
                .userId(order.getUserId())
                .orderId(order.getOrderId())
                .orderStatus(order.getOrderStatus())
                .reason(order.getReason())
                .build()
        );
    }

    @Override
    public void updateOrderStatus(CreateListOrderStatusEvent createListOrderStatusEvent) {
        createListOrderStatusEvent.getOrderStatusEventList().forEach(orderStatusEvent -> {
            Order order = orderRepository.findById(orderStatusEvent.getOrderId())
                .orElseThrow(() -> new NotFoundException(MessageError.ORDER_NOT_FOUND));
            order.setOrderStatus(orderStatusEvent.getOrderStatus());
            if (orderStatusEvent.getOrderStatus() == OrderStatus.CANCELLED || orderStatusEvent.getOrderStatus() == OrderStatus.RETURNED) {
                order.setReason(orderStatusEvent.getReason());
            }
            orderRepository.save(order);
        });

    }

    private static final Map<OrderStatus, Set<OrderStatus>> transitions = Map.of(
        OrderStatus.PENDING, Set.of(OrderStatus.PAID, OrderStatus.CANCELLED),
        OrderStatus.PAID, Set.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
        OrderStatus.CONFIRMED, Set.of(OrderStatus.DELIVERED),
        OrderStatus.DELIVERED, Set.of(OrderStatus.SHIPPED),
        OrderStatus.SHIPPED, Set.of(OrderStatus.COMPLETED, OrderStatus.RETURNED),
        OrderStatus.COMPLETED, Set.of(OrderStatus.RETURNED)
    );

}

