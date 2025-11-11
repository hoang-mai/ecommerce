package com.ecommerce.order.controller;

import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.enumeration.OrderStatus;
import com.ecommerce.order.dto.CreateOrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "APIs for managing orders")
public class OrderController {

    private final OrderService orderService;

    /**
     * Create a new order
     */
    @PostMapping
    @Operation(summary = "Create order", description = "Create a new order from cart items")
    public ResponseEntity<BaseResponse<OrderResponse>> createOrder(
            Authentication authentication,
            @Valid @RequestBody CreateOrderRequest request) {

        Long userId = Long.valueOf(authentication.getName());
        OrderResponse orderResponse = orderService.createOrder(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.<OrderResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Order created successfully")
                        .data(orderResponse)
                        .build());
    }

    /**
     * Get order by ID
     */
    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID", description = "Get order details by order ID")
    public ResponseEntity<BaseResponse<OrderResponse>> getOrderById(
            Authentication authentication,
            @PathVariable Long orderId) {

        Long userId = Long.valueOf(authentication.getName());
        OrderResponse orderResponse = orderService.getOrderById(userId, orderId);

        return ResponseEntity.ok(BaseResponse.<OrderResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Order retrieved successfully")
                .data(orderResponse)
                .build());
    }

    /**
     * Get all orders for current user
     */
    @GetMapping
    @Operation(summary = "Get user orders", description = "Get all orders for current user")
    public ResponseEntity<BaseResponse<List<OrderResponse>>> getUserOrders(
            Authentication authentication,
            @RequestParam(required = false) OrderStatus status) {

        Long userId = Long.valueOf(authentication.getName());
        List<OrderResponse> orders;

        if (status != null) {
            orders = orderService.getOrdersByUserIdAndStatus(userId, status);
        } else {
            orders = orderService.getOrdersByUserId(userId);
        }

        return ResponseEntity.ok(BaseResponse.<List<OrderResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Orders retrieved successfully")
                .data(orders)
                .build());
    }

    /**
     * Update order status
     */
    @PatchMapping("/{orderId}/status")
    @Operation(summary = "Update order status", description = "Update the status of an order")
    public ResponseEntity<BaseResponse<OrderResponse>> updateOrderStatus(
            Authentication authentication,
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {

        Long userId = Long.valueOf(authentication.getName());
        OrderResponse orderResponse = orderService.updateOrderStatus(userId, orderId, status);

        return ResponseEntity.ok(BaseResponse.<OrderResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Order status updated successfully")
                .data(orderResponse)
                .build());
    }

    /**
     * Cancel order
     */
    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel order", description = "Cancel an order")
    public ResponseEntity<BaseResponse<OrderResponse>> cancelOrder(
            Authentication authentication,
            @PathVariable Long orderId) {

        Long userId = Long.valueOf(authentication.getName());
        OrderResponse orderResponse = orderService.cancelOrder(userId, orderId);

        return ResponseEntity.ok(BaseResponse.<OrderResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Order cancelled successfully")
                .data(orderResponse)
                .build());
    }
}

