package com.ecommerce.library.kafka.event.product;

import com.ecommerce.library.enumeration.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductEvent {
    private Long productId;
    private String productName;
    private String description;
    private ProductStatus productStatus;
    private List<CreateProductImageEvent> productImages;
    private List<CreateProductAttributeEvent> productAttributes;
    private List<CreateProductVariantEvent> productVariants;
}
