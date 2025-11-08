package com.ecommerce.product.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResProductImageDTO {

    private Long productImageId;
    private String imageUrl;
}

