package com.ecommerce.flash.sale.controller;

import com.ecommerce.flash.sale.dto.ReqCreateFlashSaleCampaignScheduleDTO;
import com.ecommerce.flash.sale.dto.ReqUpdateFlashSaleCampaignScheduleStatusDTO;
import com.ecommerce.flash.sale.dto.ResFlashSaleCampaignScheduleDTO;
import com.ecommerce.flash.sale.service.FlashSaleCampaignScheduleService;
import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.enumeration.FlashSaleCampaignScheduleStatus;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.library.utils.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;

@RestController
@RequestMapping(Constant.FLASH_SALE_CAMPAIGN_SCHEDULE)
@RequiredArgsConstructor
@Tag(name = "Flash Sale Campaign Schedule Management", description = "APIs for managing flash sale campaign schedules")
public class FlashSaleCampaignScheduleController {

    private final FlashSaleCampaignScheduleService scheduleService;
    private final MessageService messageService;

    /**
     * Tạo mới lịch trình flash sale campaign
     *
     * @param request Thông tin lịch trình cần tạo
     */
    @PostMapping
    @Operation(summary = "Create flash sale campaign schedule", description = "Create a new flash sale campaign schedule")
    public ResponseEntity<BaseResponse<Void>> createSchedule(
            @Valid @RequestBody ReqCreateFlashSaleCampaignScheduleDTO request) {
        scheduleService.createSchedule(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(messageService.getMessage(MessageSuccess.FLASH_SALE_CAMPAIGN_SCHEDULE_CREATED_SUCCESS))
                .build());
    }

    /**
     * Lấy thông tin chi tiết lịch trình
     *
     * @param scheduleId ID của lịch trình
     * @return Thông tin lịch trình
     */
    @GetMapping("/{scheduleId}")
    @Operation(summary = "Get flash sale campaign schedule by ID", description = "Retrieve flash sale campaign schedule details by ID")
    public ResponseEntity<BaseResponse<ResFlashSaleCampaignScheduleDTO>> getScheduleById(
            @PathVariable Long scheduleId) {
        ResFlashSaleCampaignScheduleDTO schedule = scheduleService.getScheduleById(scheduleId);

        return ResponseEntity.ok(BaseResponse.<ResFlashSaleCampaignScheduleDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.FLASH_SALE_CAMPAIGN_SCHEDULE_RETRIEVED_SUCCESS))
                .data(schedule)
                .build());
    }

    /**
     * Cập nhật trạng thái lịch trình flash sale campaign
     *
     * @param scheduleId ID của lịch trình cần cập nhật trạng thái
     * @param request Trạng thái mới
     * @return Thông tin lịch trình đã cập nhật
     */
    @PatchMapping("/{scheduleId}/status")
    @Operation(summary = "Update flash sale campaign schedule status", description = "Update flash sale campaign schedule status (ACTIVE, INACTIVE)")
    public ResponseEntity<BaseResponse<Void>> updateScheduleStatus(
            @PathVariable Long scheduleId,
            @Valid @RequestBody ReqUpdateFlashSaleCampaignScheduleStatusDTO request) {
        scheduleService.updateScheduleStatus(
                scheduleId, request.getFlashSaleCampaignScheduleStatus());

        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.FLASH_SALE_CAMPAIGN_SCHEDULE_STATUS_UPDATED_SUCCESS))
                .build());
    }

    /**
     * Tìm kiếm lịch trình flash sale campaign
     *
     * @param status Trạng thái lịch trình
     * @param startTime Thời gian bắt đầu
     * @param endTime Thời gian kết thúc
     * @param pageNo Số trang (mặc định là 0)
     * @param pageSize Kích thước trang (mặc định là 10)
     * @param sortBy Trường sắp xếp (mặc định là startTime)
     * @param sortDir Hướng sắp xếp (asc/desc, mặc định là asc)
     * @return Danh sách lịch trình
     */
    @GetMapping()
    @Operation(summary = "Search flash sale campaign schedules", description = "Search flash sale campaign schedules with filters and pagination")
    public ResponseEntity<BaseResponse<PageResponse<ResFlashSaleCampaignScheduleDTO>>> searchSchedules(
            @RequestParam(required = false) FlashSaleCampaignScheduleStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "startTime", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir) {
        PageResponse<ResFlashSaleCampaignScheduleDTO> pageResponse = scheduleService.searchSchedules(
                status, startTime, endTime, pageNo, pageSize, sortBy, sortDir);

        return ResponseEntity.ok(BaseResponse.<PageResponse<ResFlashSaleCampaignScheduleDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.FLASH_SALE_CAMPAIGN_SCHEDULES_RETRIEVED_SUCCESS))
                .data(pageResponse)
                .build());
    }


}
