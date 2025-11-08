package com.ecommerce.product.dto;

import com.ecommerce.library.utils.MessageError;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "Request DTO for creating a product attribute")
public class ReqCreateProductAttributeDTO {

    @NotNull(message = MessageError.PRODUCT_NOT_NULL)
    @Schema(description = "ID of the product", example = "1")
    private Long productId;

    @NotBlank(message = MessageError.ATTRIBUTE_NAME_NOT_BLANK)
    @Schema(description = "Name of the attribute", example = "Color")
    private String attributeName;

    @NotEmpty(message = MessageError.ATTRIBUTE_VALUES_NOT_EMPTY)
    @Schema(description = "List of possible values for this attribute", example = "[\"Red\", \"Blue\", \"Green\"]")
    private List<String> attributeValues;
}

