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
public class ShopViewStatisticDTO {
    private String shopId;
    private String shopName;
    private BigDecimal totalRevenue;
    private Long totalSold;
    private Long totalOrder;
}

