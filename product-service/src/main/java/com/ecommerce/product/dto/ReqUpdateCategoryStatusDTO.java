package com.ecommerce.product.dto;

import com.ecommerce.library.enumeration.CategoryStatus;
import com.ecommerce.library.utils.MessageError;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReqUpdateCategoryStatusDTO {

    @NotNull(message = MessageError.CATEGORY_STATUS_NOT_NULL)
    @Schema(description = "Status of the category", example = "ACTIVE")
    private CategoryStatus categoryStatus;
}

