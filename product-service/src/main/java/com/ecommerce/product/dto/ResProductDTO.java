package com.ecommerce.product.dto;

import com.ecommerce.library.enumeration.ProductStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ResProductDTO {

    private Long productId;
    private String name;
    private String description;
    private ProductStatus productStatus;
    private Integer totalSold;
    private Double discount;
    private LocalDateTime discountStartDate;
    private LocalDateTime discountEndDate;
    private ResCategoryDTO category;
    private List<ResProductImageDTO> productImages;
    private List<ResProductAttributeDTO> productAttributes;
    private List<ResProductVariantDTO> productVariants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
