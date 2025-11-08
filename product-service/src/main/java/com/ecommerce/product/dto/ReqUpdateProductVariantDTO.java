package com.ecommerce.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ReqUpdateProductVariantDTO {

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Schema(description = "Price of the variant", example = "29999000")
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Schema(description = "Available stock quantity", example = "100")
    private Integer stockQuantity;
}

