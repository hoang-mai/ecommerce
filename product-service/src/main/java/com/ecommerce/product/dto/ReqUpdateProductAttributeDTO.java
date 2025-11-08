package com.ecommerce.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "Request DTO for updating a product attribute")
public class ReqUpdateProductAttributeDTO {

    @NotBlank(message = "Attribute name is required")
    @Schema(description = "Name of the attribute", example = "Size")
    private String attributeName;

    @NotEmpty(message = "At least one attribute value is required")
    @Schema(description = "List of possible values for this attribute", example = "[\"S\", \"M\", \"L\", \"XL\"]")
    private List<String> attributeValues;
}

