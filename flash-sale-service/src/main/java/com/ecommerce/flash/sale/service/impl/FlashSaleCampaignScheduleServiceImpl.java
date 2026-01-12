package com.ecommerce.flash.sale.service.impl;

import com.ecommerce.flash.sale.dto.ReqCreateFlashSaleCampaignScheduleDTO;
import com.ecommerce.flash.sale.dto.ResFlashSaleCampaignScheduleDTO;
import com.ecommerce.flash.sale.entity.FlashSaleCampaignSchedule;
import com.ecommerce.flash.sale.repository.FlashSaleCampaignScheduleRepository;
import com.ecommerce.flash.sale.service.FlashSaleCampaignScheduleService;
import com.ecommerce.flash.sale.service.FlashSaleCampaignService;
import com.ecommerce.library.enumeration.FlashSaleCampaignScheduleStatus;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlashSaleCampaignScheduleServiceImpl implements FlashSaleCampaignScheduleService {

    private final FlashSaleCampaignScheduleRepository scheduleRepository;
    private final FlashSaleCampaignService campaignService;

    @Override
    @Transactional
    public void createSchedule(ReqCreateFlashSaleCampaignScheduleDTO request) {

        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new IllegalArgumentException(MessageError.START_TIME_MUST_BE_BEFORE_END_TIME);
        }

        boolean overlappingSchedules = scheduleRepository.findOverlappingActiveSchedules(
                request.getStartTime(),
                request.getEndTime());

        if (overlappingSchedules) {
            throw new IllegalArgumentException(MessageError.FLASH_SALE_CAMPAIGN_SCHEDULE_CONFLICT);
        }

        FlashSaleCampaignSchedule schedule = FlashSaleCampaignSchedule.builder()
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .flashSaleCampaignScheduleStatus(FlashSaleCampaignScheduleStatus.ACTIVE)
                .build();

        scheduleRepository.save(schedule);
        campaignService.createCampaignFromSchedule(schedule);
    }

    @Override
    public void updateScheduleStatus(Long scheduleId, FlashSaleCampaignScheduleStatus status) {

        FlashSaleCampaignSchedule schedule = scheduleRepository.findByFlashSaleCampaignScheduleId(scheduleId)
                .orElseThrow(() -> new NotFoundException(MessageError.FLASH_SALE_CAMPAIGN_SCHEDULE_NOT_FOUND));


        if (status == FlashSaleCampaignScheduleStatus.ACTIVE) {
            boolean overlappingSchedules = scheduleRepository.findOverlappingActiveSchedules(
                    schedule.getStartTime(),
                    schedule.getEndTime());

            if (overlappingSchedules) {
                throw new IllegalArgumentException(MessageError.FLASH_SALE_CAMPAIGN_SCHEDULE_CONFLICT);
            }
        }

        schedule.setFlashSaleCampaignScheduleStatus(status);
        scheduleRepository.save(schedule);

    }

    @Override
    @Transactional(readOnly = true)
    public ResFlashSaleCampaignScheduleDTO getScheduleById(Long scheduleId) {

        FlashSaleCampaignSchedule schedule = scheduleRepository.findByFlashSaleCampaignScheduleId(scheduleId)
                .orElseThrow(() -> new NotFoundException(MessageError.FLASH_SALE_CAMPAIGN_SCHEDULE_NOT_FOUND));

        return convertToDTO(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ResFlashSaleCampaignScheduleDTO> searchSchedules(
            FlashSaleCampaignScheduleStatus status,
            LocalTime startTime,
            LocalTime endTime,
            int pageNo,
            int pageSize,
            String sortBy,
            String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<FlashSaleCampaignSchedule> schedulesPage = scheduleRepository.searchSchedules(status, startTime, endTime, pageable);

        return buildPageResponse(schedulesPage);
    }

    private PageResponse<ResFlashSaleCampaignScheduleDTO> buildPageResponse(Page<FlashSaleCampaignSchedule> schedulesPage) {
        List<ResFlashSaleCampaignScheduleDTO> scheduleResponses = schedulesPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return PageResponse.<ResFlashSaleCampaignScheduleDTO>builder()
                .pageNo(schedulesPage.getNumber())
                .pageSize(schedulesPage.getSize())
                .totalElements(schedulesPage.getTotalElements())
                .totalPages(schedulesPage.getTotalPages())
                .hasNextPage(schedulesPage.hasNext())
                .hasPreviousPage(schedulesPage.hasPrevious())
                .data(scheduleResponses)
                .build();
    }

    private ResFlashSaleCampaignScheduleDTO convertToDTO(FlashSaleCampaignSchedule schedule) {
        return ResFlashSaleCampaignScheduleDTO.builder()
                .flashSaleCampaignScheduleId(schedule.getFlashSaleCampaignScheduleId())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .flashSaleCampaignScheduleStatus(schedule.getFlashSaleCampaignScheduleStatus())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt())
                .build();
    }
}

