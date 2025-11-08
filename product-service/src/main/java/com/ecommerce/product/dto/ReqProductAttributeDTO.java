package com.ecommerce.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class ReqProductAttributeDTO {

    @Schema(description = "Name of the attribute", example = "Color")
    private String attributeName;

    @Schema(description = "List of possible values for this attribute", example = "[\"Red\", \"Blue\", \"Green\"]")
    private List<String> attributeValues;
}

