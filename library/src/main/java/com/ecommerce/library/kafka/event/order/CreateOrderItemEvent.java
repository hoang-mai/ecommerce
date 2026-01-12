package com.ecommerce.library.kafka.event.order;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderItemEvent {
    private Long orderItemId;
    private Long productId;
    private String productName;
    private BigDecimal totalPrice;
    private BigDecimal totalDiscount;
    private BigDecimal totalFinalPrice;
    private Long productVariantId;
    private Integer quantity;
    private BigDecimal price;
    private String productImageUrl;
    private List<CreateProductAttribute> createProductAttributeList;
    private Boolean isFlashSale;
}
