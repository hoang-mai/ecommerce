package com.ecommerce.library.kafka.event.product;

import com.ecommerce.library.enumeration.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductEvent {
    private Long productId;
    private Long shopId;
    private String productName;
    private String description;
    private ProductStatus productStatus;
    private Integer totalSold;
    private Double discount;
    private LocalDateTime discountStartDate;
    private LocalDateTime discountEndDate;
    private Long categoryId;
    private List<CreateProductImageEvent> productImages;
    private List<CreateProductAttributeEvent> productAttributes;
    private List<CreateProductVariantEvent> productVariants;
}
