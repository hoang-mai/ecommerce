package com.ecommerce.flash.sale.dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResFlashSaleCampaignDTO {

    private Long flashSaleCampaignId;

    private String campaignName;

    private String description;

    private Instant startTime;

    private Instant endTime;

    private Long flashSaleCampaignScheduleId;

    private Instant createdAt;

    private Instant updatedAt;
}

