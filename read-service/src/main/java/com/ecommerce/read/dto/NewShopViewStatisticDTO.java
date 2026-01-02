package com.ecommerce.read.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewShopViewStatisticDTO {
    private String localDate;
    private Integer newShopViews;
}
