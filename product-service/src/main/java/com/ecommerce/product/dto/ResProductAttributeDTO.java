package com.ecommerce.product.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ResProductAttributeDTO {

    private Long productAttributeId;
    private String attributeName;
    private List<ResProductAttributeValueDTO> attributeValues;
}

