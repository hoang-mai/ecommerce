package com.ecommerce.product.dto;

import com.ecommerce.library.enumeration.ProductVariantStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Builder
public class ResProductVariantDTO {

    private Long productVariantId;
    private BigDecimal price;
    private Integer stockQuantity;
    private Boolean isDefault;
    private ProductVariantStatus productVariantStatus;
    private Map<String, String> attributeValues;
}

