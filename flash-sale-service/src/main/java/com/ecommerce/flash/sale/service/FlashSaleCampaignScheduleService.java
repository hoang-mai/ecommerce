package com.ecommerce.flash.sale.service;

import com.ecommerce.flash.sale.dto.ReqCreateFlashSaleCampaignScheduleDTO;
import com.ecommerce.flash.sale.dto.ResFlashSaleCampaignScheduleDTO;
import com.ecommerce.library.enumeration.FlashSaleCampaignScheduleStatus;
import com.ecommerce.library.utils.PageResponse;

import java.time.LocalTime;

public interface FlashSaleCampaignScheduleService {

    /**
     * Tạo mới lịch trình flash sale campaign
     *
     * @param request Thông tin lịch trình
     */
    void createSchedule(ReqCreateFlashSaleCampaignScheduleDTO request);


    /**
     * Cập nhật trạng thái lịch trình flash sale campaign
     *
     * @param scheduleId ID của lịch trình
     * @param status Trạng thái mới
     */
    void updateScheduleStatus(Long scheduleId, FlashSaleCampaignScheduleStatus status);

    /**
     * Lấy thông tin chi tiết lịch trình
     *
     * @param scheduleId ID của lịch trình
     * @return Thông tin lịch trình
     */
    ResFlashSaleCampaignScheduleDTO getScheduleById(Long scheduleId);

    /**
     * Tìm kiếm lịch trình flash sale campaign
     *
     * @param status Trạng thái lịch trình
     * @param startTime Thời gian bắt đầu
     * @param endTime Thời gian kết thúc
     * @param pageNo Số trang
     * @param pageSize Kích thước trang
     * @param sortBy Trường sắp xếp
     * @param sortDir Hướng sắp xếp
     * @return Danh sách lịch trình
     */
    PageResponse<ResFlashSaleCampaignScheduleDTO> searchSchedules(
            FlashSaleCampaignScheduleStatus status,
            LocalTime startTime,
            LocalTime endTime,
            int pageNo,
            int pageSize,
            String sortBy,
            String sortDir);

}

