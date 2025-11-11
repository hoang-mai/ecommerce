package com.ecommerce.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCartItemRequest {

    @Positive(message = "Quantity must be positive")
    @Schema(description = "New quantity", example = "3")
    private Integer quantity;
}

