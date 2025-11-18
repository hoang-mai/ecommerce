package com.ecommerce.library.kafka.event.order;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductOrderItemEvent {
    private Long productVariantId;
    private Integer quantity;
    private BigDecimal price;
    private List<CreateProductAttribute> createProductAttributeList;
}
