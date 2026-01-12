package com.ecommerce.flash.sale.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResPurchaseLimitCheckDTO {
    private Long productVariantId;
    private Integer purchasedQuantity;
    private Integer maxQuantityPerUser;
    private Integer remainingQuantity;
    private Boolean isExceeded;
}

