package com.ecommerce.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ReqUpdateProductAttributeValueDTO {

    @Schema(description = "Attribute value", example = "Đỏ")
    private String attributeValue;

    @Schema(description = "Attribute value ID (nếu có = update, không có = thêm mới)", example = "1")
    private Long attributeValueId;

}
