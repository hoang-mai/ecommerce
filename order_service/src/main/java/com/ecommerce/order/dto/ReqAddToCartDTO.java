package com.ecommerce.order.dto;

import com.ecommerce.library.utils.MessageError;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ReqAddToCartDTO {

    @NotNull(message = MessageError.PRODUCT_NOT_NULL)
    @Schema(description = "Product ID", example = "1")
    private Long productId;

    @NotNull(message = MessageError.PRODUCT_VARIANT_NOT_NULL)
    @Schema(description = "Product variant ID", example = "1")
    private Long productVariantId;

    @NotNull(message = MessageError.QUANTITY_NOT_NULL)
    @Positive(message = MessageError.QUANTITY_POSITIVE)
    @Schema(description = "Quantity to add", example = "2")
    private Integer quantity;
}

