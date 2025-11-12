package com.ecommerce.library.kafka.event.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductAttributeEvent {
    private Long productAttributeId;
    private Long productId;
    private String productAttributeName;
    private List<CreateProductAttributeValueEvent> productAttributeValues;
}
