package com.ecommerce.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemRequest {

    @NotNull(message = "Product ID is required")
    @Schema(description = "Product ID", example = "1")
    private Long productId;

    @NotNull(message = "Product variant ID is required")
    @Schema(description = "Product variant ID", example = "1")
    private Long productVariantId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    @Schema(description = "Quantity to order", example = "2")
    private Integer quantity;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Schema(description = "Price per unit", example = "100000")
    private BigDecimal price;
}

