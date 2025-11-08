package com.ecommerce.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request DTO for creating a product image")
public class ReqCreateProductImageDTO {

    @NotNull(message = "Product ID is required")
    @Schema(description = "ID of the product", example = "1")
    private Long productId;

    @NotBlank(message = "Image URL is required")
    @Schema(description = "URL of the product image", example = "https://example.com/images/product1.jpg")
    private String imageUrl;
}

