package com.ecommerce.flash.sale.dto;

import com.ecommerce.library.enumeration.FlashSaleCampaignScheduleStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReqUpdateFlashSaleCampaignScheduleStatusDTO {

    @NotNull(message = "Status is required")
    private FlashSaleCampaignScheduleStatus flashSaleCampaignScheduleStatus;
}

