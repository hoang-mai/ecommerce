package com.ecommerce.order.mapper;

import com.ecommerce.order.dto.CartDTO;
import com.ecommerce.order.dto.CartItemDTO;
import com.ecommerce.order.dto.OrderItemResponse;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.entity.Cart;
import com.ecommerce.order.entity.CartItem;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public CartDTO toCartDTO(Cart cart) {
        if (cart == null) {
            return null;
        }

        CartDTO dto = CartDTO.builder()
                .cartId(cart.getCartId())
                .userId(cart.getUserId())
                .build();

        if (cart.getCartItems() != null) {
            dto.setCartItems(cart.getCartItems().stream()
                    .map(this::toCartItemDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public CartItemDTO toCartItemDTO(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }

        return CartItemDTO.builder()
                .cartItemId(cartItem.getCartItemId())
                .productId(cartItem.getProductId())
                .productVariantId(cartItem.getProductVariantId())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getPrice())
                .shopId(cartItem.getShopId())
                .build();
    }

    public OrderResponse toOrderResponse(Order order) {
        if (order == null) {
            return null;
        }

        OrderResponse response = OrderResponse.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .paymentId(order.getPaymentId())
                .shippingAddressId(order.getShippingAddressId())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();

        if (order.getItems() != null) {
            response.setItems(order.getItems().stream()
                    .map(this::toOrderItemResponse)
                    .collect(Collectors.toList()));
        }

        return response;
    }

    public OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }

        return OrderItemResponse.builder()
                .orderItemId(orderItem.getOrderItemId())
                .productId(orderItem.getProductId())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .build();
    }
}

