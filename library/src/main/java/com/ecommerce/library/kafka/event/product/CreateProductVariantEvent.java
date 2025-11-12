package com.ecommerce.library.kafka.event.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductVariantEvent {
    private Long productVariantId;
    private Long productId;
    private BigDecimal price;
    private Map<String, String> attributeValues;
}
