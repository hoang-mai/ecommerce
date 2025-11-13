package com.ecommerce.product.dto;

import com.ecommerce.library.enumeration.ProductVariantStatus;
import com.ecommerce.library.utils.MessageError;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReqUpdateProductVariantStatusDTO {

    @NotNull(message = MessageError.PRODUCT_STATUS_NOT_NULL)
    @Schema(description = "New status for the product", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "OUT_OF_STOCK"})
    private ProductVariantStatus productVariantStatus;
}

