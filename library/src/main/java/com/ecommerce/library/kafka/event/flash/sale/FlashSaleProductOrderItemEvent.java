package com.ecommerce.library.kafka.event.flash.sale;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FlashSaleProductOrderItemEvent {
    private Long productId;
    private Double discount;
    private Long productVariantId;
    private Integer quantity;
    private BigDecimal price;
    private Boolean isFlashSale;
}

