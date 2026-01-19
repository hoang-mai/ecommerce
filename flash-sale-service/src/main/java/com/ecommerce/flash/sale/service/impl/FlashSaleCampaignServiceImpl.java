package com.ecommerce.flash.sale.service.impl;

import com.ecommerce.flash.sale.dto.ReqCreateFlashSaleCampaignDTO;
import com.ecommerce.flash.sale.dto.ResFlashSaleCampaignDTO;
import com.ecommerce.flash.sale.entity.FlashSaleCampaign;
import com.ecommerce.flash.sale.entity.FlashSaleCampaignSchedule;
import com.ecommerce.flash.sale.repository.FlashSaleCampaignRepository;
import com.ecommerce.flash.sale.service.FlashSaleCampaignService;
import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlashSaleCampaignServiceImpl implements FlashSaleCampaignService {

    private final FlashSaleCampaignRepository campaignRepository;
    private final UserHelper userHelper;

    @Override
    public void createCampaign(ReqCreateFlashSaleCampaignDTO request) {

        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new IllegalArgumentException(MessageError.START_TIME_MUST_BE_BEFORE_END_TIME);
        }

        boolean hasOverlap = campaignRepository.findOverlappingCampaigns(
            request.getStartTime(),
            request.getEndTime()
        );

        if (hasOverlap) {
            throw new IllegalArgumentException(MessageError.FLASH_SALE_CAMPAIGN_SCHEDULE_CONFLICT);
        }

        FlashSaleCampaign campaign = FlashSaleCampaign.builder()
            .campaignName(request.getCampaignName())
            .description(request.getDescription())
            .startTime(request.getStartTime())
            .endTime(request.getEndTime())
            .build();

        campaignRepository.save(campaign);
    }

    @Override
    public void createCampaignFromSchedule(FlashSaleCampaignSchedule schedule) {

        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");

        LocalDate tomorrow = LocalDate.now(zoneId).plusDays(1);

        Instant startDateTime = tomorrow
            .atTime(schedule.getStartTime())
            .atZone(zoneId)
            .toInstant();

        Instant endDateTime = tomorrow
            .atTime(schedule.getEndTime())
            .atZone(zoneId)
            .toInstant();


        boolean hasOverlap = campaignRepository.findOverlappingCampaigns(
            startDateTime,
            endDateTime
        );

        if (hasOverlap) {
            throw new IllegalArgumentException(MessageError.FLASH_SALE_CAMPAIGN_SCHEDULE_CONFLICT);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String campaignName = String.format("Flash Sale - %s đến %s",
            schedule.getStartTime().format(formatter),
            schedule.getEndTime().format(formatter));

        FlashSaleCampaign campaign = FlashSaleCampaign.builder()
            .campaignName(campaignName)
            .description("Tự động tạo từ lịch trình flash sale")
            .startTime(startDateTime)
            .endTime(endDateTime)
            .flashSaleCampaignSchedule(schedule)
            .build();

        campaignRepository.save(campaign);
    }


    @Override
    public ResFlashSaleCampaignDTO getCampaignById(Long campaignId) {

        FlashSaleCampaign campaign = campaignRepository.findByFlashSaleCampaignId(campaignId)
            .orElseThrow(() -> new NotFoundException(MessageError.FLASH_SALE_CAMPAIGN_NOT_FOUND));

        return convertToDTO(campaign);
    }

    @Override
    public PageResponse<ResFlashSaleCampaignDTO> searchCampaigns(
        Boolean isOwner,
        Long scheduleId,
        String keyword,
        int pageNo,
        int pageSize,
        String sortBy,
        String sortDir) {
        Long ownerId = null;
        if (Boolean.TRUE.equals(isOwner)) {
            ownerId = userHelper.getCurrentUserId();
        }

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<FlashSaleCampaign> campaignsPage = campaignRepository.searchCampaigns(ownerId, scheduleId, keyword, pageable);

        return buildPageResponse(campaignsPage);
    }

    @Override
    public PageResponse<ResFlashSaleCampaignDTO> getCampaignsByDate(
        Instant date,
        Boolean isOwner,
        int pageNo,
        int pageSize,
        String sortBy,
        String sortDir) {

        Long ownerId = null;
        if (Boolean.TRUE.equals(isOwner)) {
            ownerId = userHelper.getCurrentUserId();
        }

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();
        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Instant startOfDay = date
            .atZone(zoneId)
            .toLocalDate()
            .atStartOfDay(zoneId)
            .toInstant();
        Instant endOfDay = date
            .atZone(zoneId)
            .toLocalDate()
            .plusDays(1)
            .atStartOfDay(zoneId)
            .toInstant();

        Page<FlashSaleCampaign> campaignsPage = campaignRepository.findCampaignsByDate(ownerId,
            startOfDay, endOfDay, pageable);

        return buildPageResponse(campaignsPage);
    }

    @Override
    public PageResponse<ResFlashSaleCampaignDTO> getGoingAndUpcomingCampaigns( int pageNo, int pageSize, String sortBy, String sortDir) {


        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Instant now = Instant.now();
        Instant endOfDay = now.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDate()
            .plusDays(1)
            .atStartOfDay(ZoneId.of("Asia/Ho_Chi_Minh"))
            .toInstant();
        Page<FlashSaleCampaign> campaignsPage = campaignRepository.findGoingAndUpcomingCampaigns( now,endOfDay, pageable);

        return buildPageResponse(campaignsPage);
    }

    private PageResponse<ResFlashSaleCampaignDTO> buildPageResponse(Page<FlashSaleCampaign> campaignsPage) {
        List<ResFlashSaleCampaignDTO> campaignResponses = campaignsPage.getContent().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());

        return PageResponse.<ResFlashSaleCampaignDTO>builder()
            .pageNo(campaignsPage.getNumber())
            .pageSize(campaignsPage.getSize())
            .totalElements(campaignsPage.getTotalElements())
            .totalPages(campaignsPage.getTotalPages())
            .hasNextPage(campaignsPage.hasNext())
            .hasPreviousPage(campaignsPage.hasPrevious())
            .data(campaignResponses)
            .build();
    }

    private ResFlashSaleCampaignDTO convertToDTO(FlashSaleCampaign campaign) {
        return ResFlashSaleCampaignDTO.builder()
            .flashSaleCampaignId(campaign.getFlashSaleCampaignId())
            .campaignName(campaign.getCampaignName())
            .description(campaign.getDescription())
            .startTime(campaign.getStartTime())
            .endTime(campaign.getEndTime())
            .countRegisteredProducts(campaign.getCountRegisteredProducts())
            .flashSaleCampaignScheduleId(campaign.getFlashSaleCampaignSchedule() != null
                ? campaign.getFlashSaleCampaignSchedule().getFlashSaleCampaignScheduleId()
                : null)
            .createdAt(campaign.getCreatedAt())
            .updatedAt(campaign.getUpdatedAt())
            .build();
    }
}

