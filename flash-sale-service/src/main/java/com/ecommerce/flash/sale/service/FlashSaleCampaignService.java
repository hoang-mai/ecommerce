package com.ecommerce.flash.sale.service;

import com.ecommerce.flash.sale.dto.ReqCreateFlashSaleCampaignDTO;
import com.ecommerce.flash.sale.dto.ResFlashSaleCampaignDTO;
import com.ecommerce.flash.sale.entity.FlashSaleCampaignSchedule;
import com.ecommerce.library.utils.PageResponse;

import java.time.Instant;
import java.time.LocalDate;

public interface FlashSaleCampaignService {

    /**
     * Tạo mới flash sale campaign
     *
     * @param request Thông tin campaign
     */
    void createCampaign(ReqCreateFlashSaleCampaignDTO request);

    /**
     * Tạo flash sale campaign từ schedule
     *
     * @param schedule Lịch trình campaign
     */
    void createCampaignFromSchedule(FlashSaleCampaignSchedule schedule);

    /**
     * Lấy thông tin chi tiết campaign
     *
     * @param campaignId ID của campaign
     * @return Thông tin campaign
     */
    ResFlashSaleCampaignDTO getCampaignById(Long campaignId);

    /**
     * Tìm kiếm flash sale campaigns
     *
     * @param scheduleId ID của schedule
     * @param keyword    Từ khóa tìm kiếm
     * @param pageNo     Số trang
     * @param pageSize   Kích thước trang
     * @param sortBy     Trường sắp xếp
     * @param sortDir    Hướng sắp xếp
     * @return Danh sách campaigns
     */
    PageResponse<ResFlashSaleCampaignDTO> searchCampaigns(
        Boolean isOwner,
        Long scheduleId,
        String keyword,
        int pageNo,
        int pageSize,
        String sortBy,
        String sortDir);

    /**
     * Lấy danh sách flash sale campaigns trong ngày chỉ định
     *
     * @param date     Ngày cần lấy campaigns
     * @param pageNo   Số trang
     * @param pageSize Kích thước trang
     * @param sortBy   Trường sắp xếp
     * @param sortDir  Hướng sắp xếp
     * @return Danh sách campaigns trong ngày
     */
    PageResponse<ResFlashSaleCampaignDTO> getCampaignsByDate(
        Instant date,
        Boolean isOwner,
        int pageNo,
        int pageSize,
        String sortBy,
        String sortDir);

    PageResponse<ResFlashSaleCampaignDTO> getGoingAndUpcomingCampaigns( int pageNo, int pageSize, String sortBy, String sortDir);
}
