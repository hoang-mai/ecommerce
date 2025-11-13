package com.ecommerce.library.kafka.event.product;

import com.ecommerce.library.enumeration.ProductVariantStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductVariantStatusEvent {
    private Long productVariantId;
    private ProductVariantStatus productVariantStatus;
}
