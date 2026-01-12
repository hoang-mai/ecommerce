package com.ecommerce.flash.sale.entity;

import com.ecommerce.library.entity.BaseEntity;
import com.ecommerce.library.enumeration.FlashSaleCampaignScheduleStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "flash_sale_campaign_schedules")
public class FlashSaleCampaignSchedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "flash_sale_campaign_schedule_id")
    private Long flashSaleCampaignScheduleId;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "flash_sale_campaign_schedule_status", nullable = false)
    private FlashSaleCampaignScheduleStatus flashSaleCampaignScheduleStatus;

    @OneToMany(mappedBy = "flashSaleCampaignSchedule", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<FlashSaleCampaign> flashSaleCampaigns;
}
