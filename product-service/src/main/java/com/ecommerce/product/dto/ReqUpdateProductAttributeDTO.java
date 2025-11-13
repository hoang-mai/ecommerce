package com.ecommerce.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Getter;

import java.util.List;

@Getter
public class ReqUpdateProductAttributeDTO {

    @Schema(description = "Product attribute ID (nếu có = update, không có = thêm mới)", example = "1")
    private Long productAttributeId;

    @Schema(description = "Name of the attribute", example = "Color")
    private String attributeName;

    @Valid
    private List<ReqUpdateProductAttributeValueDTO> attributeValues;
}
