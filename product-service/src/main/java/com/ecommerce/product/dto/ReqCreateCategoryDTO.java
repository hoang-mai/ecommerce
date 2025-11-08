package com.ecommerce.product.dto;

import com.ecommerce.library.utils.MessageError;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReqCreateCategoryDTO {

    @NotBlank(message = MessageError.CATEGORY_NAME_NOT_BLANK)
    @Schema(description = "Name of the category", example = "Electronics")
    private String name;

    @Schema(description = "Description of the category", example = "Electronic devices and accessories")
    private String description;

    @Schema(description = "Parent category ID if this is a subcategory", example = "1")
    private Long parentCategoryId;
}
