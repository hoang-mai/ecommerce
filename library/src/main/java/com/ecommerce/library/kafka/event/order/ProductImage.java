package com.ecommerce.library.kafka.event.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductImage {
    private Long productImageId;
    private String imageUrl;
}
