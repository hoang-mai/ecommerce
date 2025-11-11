package com.ecommerce.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToCartRequest {

    @NotNull(message = "Product ID is required")
    @Schema(description = "Product ID", example = "1")
    private Long productId;

    @NotNull(message = "Product variant ID is required")
    @Schema(description = "Product variant ID", example = "1")
    private Long productVariantId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    @Schema(description = "Quantity to add", example = "2")
    private Integer quantity;

    @NotNull(message = "Shop ID is required")
    @Schema(description = "Shop ID", example = "1")
    private Long shopId;
}

