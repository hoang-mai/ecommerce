package com.ecommerce.product.dto;

import com.ecommerce.library.utils.MessageError;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
public class ReqProductVariantDTO {

    @NotNull(message = MessageError.PRICE_NOT_NULL)
    @Positive(message = "Price must be positive")
    @Schema(description = "Price of the variant", example = "29999000")
    private BigDecimal price;

    @NotNull(message = MessageError.STOCK_QUANTITY_NOT_NULL)
    @PositiveOrZero(message = MessageError.STOCK_QUANTITY_POSITIVE_OR_ZERO)
    @Schema(description = "Available stock quantity", example = "100")
    private Integer stockQuantity;

    @Schema(description = "Whether this is the default variant to display", example = "true")
    private Boolean isDefault ;

    @Schema(description = "Map of attribute name to value", example = "{\"Color\": \"Red\", \"Size\": \"128GB\"}")
    private Map<String, String> attributeValues;
}
