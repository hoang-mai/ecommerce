package com.ecommerce.flash.sale.entity;

import com.ecommerce.library.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "flash_sale_campaigns")
public class FlashSaleCampaign extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "flash_sale_campaign_id")
    private Long flashSaleCampaignId;

    @Column(name = "campaign_name", nullable = false, length = 200)
    private String campaignName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Column(name = "count_registered_products", nullable = false)
    @Builder.Default
    private Long countRegisteredProducts = 0L;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "flashSaleCampaign")
    @Builder.Default
    private List<FlashSaleProduct> flashSaleProducts = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flash_sale_campaign_schedule_id")
    private FlashSaleCampaignSchedule flashSaleCampaignSchedule;

    public void addFlashSaleProduct(FlashSaleProduct flashSaleProduct) {
        this.flashSaleProducts.add(flashSaleProduct);
        flashSaleProduct.setFlashSaleCampaign(this);
    }
}
