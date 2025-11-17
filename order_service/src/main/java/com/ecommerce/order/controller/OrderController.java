package com.ecommerce.order.controller;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.enumeration.OrderStatus;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.order.dto.ReqUpdateOrderStatus;
import com.ecommerce.order.dto.ResCreateOrderDTO;
import com.ecommerce.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = Constant.ORDER)
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "APIs for managing orders")
public class OrderController {

    private final OrderService orderService;
    private final MessageService messageService;

    /**
     * Create a new order
     */
    @PostMapping
    @Operation(summary = "Create order", description = "Create a new order from cart items")
    public ResponseEntity<BaseResponse<Void>> createOrder(@Valid @RequestBody ResCreateOrderDTO request) {

        orderService.createOrder(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.<Void>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message(messageService.getMessage(MessageSuccess.ORDER_CREATED_SUCCESS))
                        .build());
    }

    /**
     * Update order status
     */
    @PatchMapping("/{orderId}/status")
    @Operation(summary = "Update order status", description = "Update the status of an order")
    public ResponseEntity<BaseResponse<Void>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody @Valid ReqUpdateOrderStatus reqUpdateOrderStatus) {

        orderService.updateOrderStatus(orderId, reqUpdateOrderStatus);

        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message(MessageSuccess.UPDATE_ORDER_STATUS_SUCCESS)

                .build());
    }
}

