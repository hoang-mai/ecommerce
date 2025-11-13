package com.ecommerce.library.kafka.event.product;

import com.ecommerce.library.enumeration.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductStatusEvent {
    private Long productId;
    private ProductStatus productStatus;
}
