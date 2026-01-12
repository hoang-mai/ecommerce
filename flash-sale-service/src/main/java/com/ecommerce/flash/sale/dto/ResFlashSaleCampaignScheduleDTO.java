package com.ecommerce.flash.sale.dto;

import com.ecommerce.library.enumeration.FlashSaleCampaignScheduleStatus;
import lombok.*;

import java.time.Instant;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResFlashSaleCampaignScheduleDTO {

    private Long flashSaleCampaignScheduleId;

    private LocalTime startTime;

    private LocalTime endTime;

    private FlashSaleCampaignScheduleStatus flashSaleCampaignScheduleStatus;

    private Instant createdAt;
    private Instant updatedAt;
}

