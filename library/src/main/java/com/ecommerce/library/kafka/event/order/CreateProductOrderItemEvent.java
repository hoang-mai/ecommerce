package com.ecommerce.library.kafka.event.order;

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
public class CreateProductOrderItemEvent {
    private Long productVariantId;
    private Integer quantity;
    private BigDecimal price;
    private List<ProductAttribute> productAttributeList;
}
