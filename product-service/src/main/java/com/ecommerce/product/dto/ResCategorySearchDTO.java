package com.ecommerce.product.dto;

import com.ecommerce.library.enumeration.CategoryStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ResCategorySearchDTO {

    private Long categoryId;
    private String categoryName;
    private String description;
    private CategoryStatus categoryStatus;
    private String parentCategoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

