package com.ecommerce.product.dto;

import com.ecommerce.library.utils.MessageError;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
public class ReqUpdateProductVariantDTO {

    @Schema(description = "Product variant ID (nếu có = update, không có = thêm mới)", example = "1")
    private Long productVariantId;

    @Positive(message = "Price must be positive")
    @Schema(description = "Price of the variant", example = "29999000")
    private BigDecimal price;

    @Schema(description = "Available stock quantity", example = "100")
    private Integer stockQuantity;

    @Schema(description = "Whether this is the default variant to display", example = "true")
    private Boolean isDefault;

    @Schema(description = "Map of attribute name to value", example = "{\"Color\": \"Red\", \"Size\": \"128GB\"}")
    private Map<String, String> attributeValues;
}
