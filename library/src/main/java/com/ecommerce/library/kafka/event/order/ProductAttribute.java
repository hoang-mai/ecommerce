package com.ecommerce.library.kafka.event.order;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductAttribute {
    private String attributeName;
    private String attributeValue;
}
