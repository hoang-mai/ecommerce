package com.ecommerce.library.kafka.event.order;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductAttribute {
    private String attributeName;
    private String attributeValue;
}
