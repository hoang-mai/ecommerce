package com.ecommerce.library.kafka.event.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductAttributeValueEvent {
    private Long attributeValueId;
    private Long attributeId;
    private String value;
}
