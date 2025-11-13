package com.ecommerce.library.kafka.event.product;

import com.ecommerce.library.enumeration.ProductVariantStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductVariantEvent {
    private Long productVariantId;
    private Long productId;
    private BigDecimal price;
    private Integer stockQuantity;
    private Boolean isDefault;
    private ProductVariantStatus productVariantStatus;
    private List<CreateProductVariantValueEvent> productVariantAttributeValues;
}
