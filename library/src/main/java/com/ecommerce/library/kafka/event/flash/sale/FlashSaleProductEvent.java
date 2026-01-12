package com.ecommerce.library.kafka.event.flash.sale;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FlashSaleProductEvent {
    private Long flashSaleCampaignId;

    private String flashSaleCampaignName;

    private Instant startTime;

    private Instant endTime;

    private Long flashSaleProductId;

    private Long ownerId;

    private Long shopId;

    private Long productId;

    private Long productVariantId;

    private BigDecimal originalPrice;

    private Double discountPercentage;

    private Integer totalQuantity;

    private Integer soldQuantity;

    private Integer maxQuantityPerUser;

    private Boolean isSoldOut;

    private Double score;

    private Instant createdAt;

    private Integer totalSold;

    private Double rating;

}
