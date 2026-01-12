package com.ecommerce.flash.sale.repository;

import com.ecommerce.flash.sale.entity.FlashSaleCampaignSchedule;
import com.ecommerce.library.enumeration.FlashSaleCampaignScheduleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlashSaleCampaignScheduleRepository extends JpaRepository<FlashSaleCampaignSchedule, Long> {

    @Query("SELECT f FROM FlashSaleCampaignSchedule f WHERE " +
            "(:status IS NULL OR f.flashSaleCampaignScheduleStatus = :status) AND " +
            "(:startTime IS NULL OR f.startTime >= :startTime) AND " +
            "(:endTime IS NULL OR f.endTime <= :endTime)")
    Page<FlashSaleCampaignSchedule> searchSchedules(
            @Param("status") FlashSaleCampaignScheduleStatus status,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            Pageable pageable);

    Optional<FlashSaleCampaignSchedule> findByFlashSaleCampaignScheduleId(Long scheduleId);

    List<FlashSaleCampaignSchedule> findByFlashSaleCampaignScheduleStatus(FlashSaleCampaignScheduleStatus status);

    /**
     * Tìm các schedule ACTIVE có thời gian trùng lặp với khoảng thời gian cho trước
     * Trùng lặp xảy ra khi:
     * - startTime của schedule mới nằm giữa startTime và endTime của schedule hiện có
     * - endTime của schedule mới nằm giữa startTime và endTime của schedule hiện có
     * - schedule mới bao phủ hoàn toàn schedule hiện có
     *
     * @param startTime Thời gian bắt đầu
     * @param endTime Thời gian kết thúc
     * @return Danh sách các schedule bị trùng lặp
     */
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
            "FROM FlashSaleCampaignSchedule f " +
            "WHERE " +
            "f.flashSaleCampaignScheduleStatus = 'ACTIVE' AND " +
            "((f.startTime < :endTime AND f.endTime > :startTime))")
    boolean findOverlappingActiveSchedules(
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);
}

