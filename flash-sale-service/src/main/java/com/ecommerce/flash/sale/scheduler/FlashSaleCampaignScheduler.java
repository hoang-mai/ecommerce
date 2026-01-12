package com.ecommerce.flash.sale.scheduler;

import com.ecommerce.flash.sale.entity.FlashSaleCampaignSchedule;
import com.ecommerce.flash.sale.repository.FlashSaleCampaignScheduleRepository;
import com.ecommerce.flash.sale.service.FlashSaleCampaignService;
import com.ecommerce.library.enumeration.FlashSaleCampaignScheduleStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Scheduled job để tự động tạo Flash Sale Campaign từ các Schedule có trạng thái ACTIVE
 * Chạy mỗi ngày lúc 11:00 AM
 */
@Component
@RequiredArgsConstructor
public class FlashSaleCampaignScheduler {

    private final FlashSaleCampaignScheduleRepository scheduleRepository;
    private final FlashSaleCampaignService campaignService;

    /**
     * Scheduled job chạy mỗi ngày lúc 00:00:00
     * Lấy tất cả các FlashSaleCampaignSchedule có trạng thái ACTIVE
     * và tạo FlashSaleCampaign tương ứng cho từng schedule
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void createCampaignsFromActiveSchedules() {

        List<FlashSaleCampaignSchedule> activeSchedules =
            scheduleRepository.findByFlashSaleCampaignScheduleStatus(FlashSaleCampaignScheduleStatus.ACTIVE);
        if (activeSchedules.isEmpty()) {
            return;
        }


        for (FlashSaleCampaignSchedule schedule : activeSchedules) {
            campaignService.createCampaignFromSchedule(schedule);
        }
    }
}

