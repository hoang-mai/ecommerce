package com.ecommerce.read.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnerViewStatisticDTO {
    private BigDecimal totalRevenue;
    private Integer totalProducts;
    private Integer totalOrders;
    private Integer totalSold;
}
