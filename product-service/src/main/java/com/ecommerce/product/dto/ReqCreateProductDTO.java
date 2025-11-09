package com.ecommerce.product.dto;

import com.ecommerce.library.enumeration.ProductStatus;
import com.ecommerce.library.utils.MessageError;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class ReqCreateProductDTO {

    @NotNull(message = MessageError.SHOP_NOT_NULL)
    @Schema(description = "ID of the shop that owns this product", example = "1")
    private Long shopId;

    @NotBlank(message = MessageError.PRODUCT_NAME_NOT_BLANK)
    @Schema(description = "Name of the product", example = "iPhone 15 Pro Max")
    private String name;

    @Schema(description = "Description of the product", example = "Latest iPhone with advanced features")
    private String description;

    @NotNull(message = MessageError.CATEGORY_NOT_NULL)
    @Schema(description = "Category ID for the product", example = "1")
    private Long categoryId;

    @NotNull(message = MessageError.PRODUCT_ATTRIBUTES_NOT_NULL)
    @Schema(description = "List of product attributes (e.g., Color, Size)")
    private List<ReqProductAttributeDTO> productAttributes;

    @NotNull(message = MessageError.PRODUCT_VARIANTS_NOT_NULL)
    @Schema(description = "List of product variants with pricing and stock")
    private List<ReqProductVariantDTO> productVariants;
}

