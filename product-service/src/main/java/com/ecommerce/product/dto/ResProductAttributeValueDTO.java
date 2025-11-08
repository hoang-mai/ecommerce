package com.ecommerce.product.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResProductAttributeValueDTO {

    private Long attributeValueId;
    private String attributeValue;
}

