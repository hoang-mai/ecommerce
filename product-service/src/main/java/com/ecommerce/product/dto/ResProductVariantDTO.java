package com.ecommerce.product.dto;

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
    private Map<String, String> attributeValues;
}

