package com.ecommerce.read.dto;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class FlashSaleStatisticDTO {
    private String flashSaleCampaignId;
    private String flashSaleCampaignName;
    private Instant startTime;
    private Instant endTime;
    private Long totalQuantity;
    private Long totalSoldQuantity;
    private Double soldRate;
    private BigDecimal totalRevenue;

}
