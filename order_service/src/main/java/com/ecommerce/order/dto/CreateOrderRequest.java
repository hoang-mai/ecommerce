package com.ecommerce.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateOrderRequest {

    @NotNull(message = "Shipping address ID is required")
    @Schema(description = "Shipping address ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID shippingAddressId;

    @NotEmpty(message = "Order items cannot be empty")
    @Valid
    @Schema(description = "List of items to order")
    private List<OrderItemRequest> items;
}

