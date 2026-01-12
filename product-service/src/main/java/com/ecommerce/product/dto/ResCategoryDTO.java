package com.ecommerce.product.dto;

import com.ecommerce.library.enumeration.CategoryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ResCategoryDTO {

    private Long categoryId;
    private String categoryName;
    private String description;
    private CategoryStatus categoryStatus;
    private int countChildren;
    private Instant createdAt;
    private Instant updatedAt;
    private List<ResCategoryDTO> subCategories;
    private ResCategoryDTO parentCategory;
}
