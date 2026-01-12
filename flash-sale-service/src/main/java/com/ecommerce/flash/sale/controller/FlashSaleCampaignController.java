package com.ecommerce.flash.sale.controller;

import com.ecommerce.flash.sale.dto.ReqCreateFlashSaleCampaignDTO;
import com.ecommerce.flash.sale.dto.ResFlashSaleCampaignDTO;
import com.ecommerce.flash.sale.service.FlashSaleCampaignService;
import com.ecommerce.library.component.MessageService;
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

import java.time.Instant;
import java.time.LocalDate;

@RestController
@RequestMapping(Constant.FLASH_SALE_CAMPAIGN)
@RequiredArgsConstructor
@Tag(name = "Flash Sale Campaign Management", description = "APIs for managing flash sale campaigns")
public class FlashSaleCampaignController {

    private final FlashSaleCampaignService campaignService;
    private final MessageService messageService;

    /**
     * Tạo mới flash sale campaign
     *
     * @param request Thông tin campaign cần tạo
     */
    @PostMapping
    @Operation(summary = "Create flash sale campaign", description = "Create a new flash sale campaign")
    public ResponseEntity<BaseResponse<Void>> createCampaign(
            @Valid @RequestBody ReqCreateFlashSaleCampaignDTO request) {
        campaignService.createCampaign(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(messageService.getMessage(MessageSuccess.FLASH_SALE_CAMPAIGN_CREATED_SUCCESS))
                .build());
    }


    /**
     * Lấy thông tin chi tiết campaign
     *
     * @param campaignId ID của campaign
     * @return Thông tin campaign
     */
    @GetMapping("/{campaignId}")
    @Operation(summary = "Get flash sale campaign by ID", description = "Retrieve flash sale campaign details by ID")
    public ResponseEntity<BaseResponse<ResFlashSaleCampaignDTO>> getCampaignById(
            @PathVariable Long campaignId) {
        ResFlashSaleCampaignDTO campaign = campaignService.getCampaignById(campaignId);

        return ResponseEntity.ok(BaseResponse.<ResFlashSaleCampaignDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.FLASH_SALE_CAMPAIGN_RETRIEVED_SUCCESS))
                .data(campaign)
                .build());
    }


    /**
     * Tìm kiếm flash sale campaigns
     *
     * @param scheduleId ID của schedule
     * @param keyword Từ khóa tìm kiếm
     * @param pageNo Số trang (mặc định là 0)
     * @param pageSize Kích thước trang (mặc định là 10)
     * @param sortBy Trường sắp xếp (mặc định là createdAt)
     * @param sortDir Hướng sắp xếp (asc/desc, mặc định là desc)
     * @return Danh sách campaigns
     */
    @GetMapping("")
    @Operation(summary = "Search flash sale campaigns", description = "Search flash sale campaigns with filters and pagination")
    public ResponseEntity<BaseResponse<PageResponse<ResFlashSaleCampaignDTO>>> searchCampaigns(
            @RequestParam(required = false) Long scheduleId,
            @RequestParam(required = false) Boolean isOwner,
            @RequestParam(required = false) String keyword,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc", required = false) String sortDir) {
        PageResponse<ResFlashSaleCampaignDTO> pageResponse = campaignService.searchCampaigns(
            isOwner,scheduleId, keyword, pageNo, pageSize, sortBy, sortDir);

        return ResponseEntity.ok(BaseResponse.<PageResponse<ResFlashSaleCampaignDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.FLASH_SALE_CAMPAIGNS_RETRIEVED_SUCCESS))
                .data(pageResponse)
                .build());
    }

    /**
     * Lấy danh sách flash sale campaigns trong ngày chỉ định
     *
     * @param date Ngày cần lấy campaigns (format: yyyy-MM-dd)
     * @param pageNo Số trang (mặc định là 0)
     * @param pageSize Kích thước trang (mặc định là 10)
     * @param sortBy Trường sắp xếp (mặc định là startTime)
     * @param sortDir Hướng sắp xếp (asc/desc, mặc định là asc)
     * @return Danh sách campaigns trong ngày
     */
    @GetMapping("/by-date")
    @Operation(summary = "Get flash sale campaigns by date", description = "Retrieve flash sale campaigns for a specific date")
    public ResponseEntity<BaseResponse<PageResponse<ResFlashSaleCampaignDTO>>> getCampaignsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant date,
            @RequestParam(required = false) Boolean isOwner,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "startTime", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir) {
        PageResponse<ResFlashSaleCampaignDTO> pageResponse = campaignService.getCampaignsByDate(
                date,isOwner, pageNo, pageSize, sortBy, sortDir);

        return ResponseEntity.ok(BaseResponse.<PageResponse<ResFlashSaleCampaignDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.FLASH_SALE_CAMPAIGNS_BY_DATE_RETRIEVED_SUCCESS))
                .data(pageResponse)
                .build());
    }

    @GetMapping("/going-and-upcoming")
    @Operation(summary = "Get going and upcoming flash sale campaigns", description = "Retrieve going and upcoming flash sale campaigns")
    public ResponseEntity<BaseResponse<PageResponse<ResFlashSaleCampaignDTO>>> getGoingAndUpcomingCampaigns(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "startTime", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir) {
        PageResponse<ResFlashSaleCampaignDTO> pageResponse = campaignService.getGoingAndUpcomingCampaigns(
             pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(BaseResponse.<PageResponse<ResFlashSaleCampaignDTO>>builder()
            .statusCode(HttpStatus.OK.value())
            .message(messageService.getMessage(MessageSuccess.FLASH_SALE_CAMPAIGNS_BY_DATE_RETRIEVED_SUCCESS))
            .data(pageResponse)
            .build());
    }
}

