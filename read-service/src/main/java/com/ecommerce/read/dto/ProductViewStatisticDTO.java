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
public class ProductViewStatisticDTO {
    private String productId;
    private BigDecimal totalRevenue;
    private String productName;
    private Integer totalSold;
}
