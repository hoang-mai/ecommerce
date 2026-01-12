package com.ecommerce.flash.sale.repository;

import com.ecommerce.flash.sale.entity.FlashSaleCampaign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface FlashSaleCampaignRepository extends JpaRepository<FlashSaleCampaign, Long> {

    @Query("""
            SELECT f
            FROM FlashSaleCampaign f
            LEFT JOIN f.flashSaleProducts p
            LEFT JOIN f.flashSaleCampaignSchedule s
            WHERE
                (:ownerId IS NULL OR p.ownerId = :ownerId)
                AND (:scheduleId IS NULL OR s.flashSaleCampaignScheduleId = :scheduleId)
                AND (:keyword IS NULL
                OR LOWER(f.campaignName) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """)
    Page<FlashSaleCampaign> searchCampaigns(
        @Param("ownerId") Long ownerId,
        @Param("scheduleId") Long scheduleId,
        @Param("keyword") String keyword,
        Pageable pageable);

    Optional<FlashSaleCampaign> findByFlashSaleCampaignId(Long campaignId);

    /**
     * Tìm các campaign có trạng thái SCHEDULED và có thời gian trùng với khoảng thời gian cho trước
     * Hai khoảng thời gian trùng nhau khi:
     * - startTime1 < endTime2 AND endTime1 > startTime2
     */
    @Query("""
            SELECT COUNT(f) > 0
            FROM FlashSaleCampaign f
            WHERE f.startTime < :endTime
              AND f.endTime > :startTime
        """)
    boolean findOverlappingCampaigns(
        @Param("startTime") Instant startTime,
        @Param("endTime") Instant endTime);

    /**
     * Tìm các campaign trong ngày chỉ định
     * Campaign được tính là trong ngày nếu:
     * - startTime hoặc endTime nằm trong ngày đó
     * - hoặc campaign kéo dài qua ngày đó (startTime trước ngày, endTime sau ngày)
     */
    @Query("""
            SELECT f FROM FlashSaleCampaign f
            LEFT JOIN f.flashSaleProducts p
            WHERE (:ownerId IS NULL OR p.ownerId = :ownerId)
              AND ((f.startTime >= :startOfDay AND f.startTime < :endOfDay)
               OR (f.endTime > :startOfDay AND f.endTime <= :endOfDay)
               OR (f.startTime < :startOfDay AND f.endTime > :endOfDay))
        """)
    Page<FlashSaleCampaign> findCampaignsByDate(
        @Param("ownerId") Long ownerId,
        @Param("startOfDay") Instant startOfDay,
        @Param("endOfDay") Instant endOfDay,
        Pageable pageable);

    @Query("""
            SELECT f FROM FlashSaleCampaign f
            WHERE f.endTime > :now AND f.endTime < :endOfDay
        """)
    Page<FlashSaleCampaign> findGoingAndUpcomingCampaigns(Instant now,Instant endOfDay, Pageable pageable);
}
