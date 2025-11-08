package com.ecommerce.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "Request DTO for updating a product image")
public class ReqUpdateProductImageDTO {

    @NotBlank(message = "Image URL is required")
    @Schema(description = "URL of the product image", example = "https://example.com/images/product1-updated.jpg")
    private String imageUrl;
}

