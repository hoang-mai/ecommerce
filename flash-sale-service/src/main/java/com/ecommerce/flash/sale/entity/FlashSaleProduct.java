package com.ecommerce.flash.sale.entity;

import com.ecommerce.library.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "flash_sale_products")
public class FlashSaleProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "flash_sale_product_id")
    private Long flashSaleProductId;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_variant_id", nullable = false)
    private Long productVariantId;

    @Column(name = "discount_percentage", nullable = false)
    private Double discountPercentage;

    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity;

    @Column(name = "sold_quantity", nullable = false)
    private Integer soldQuantity;

    @Column(name = "max_quantity_per_user", nullable = false)
    private Integer maxQuantityPerUser;

    @Column(name = "is_sold_out", nullable = false)
    @Builder.Default
    private Boolean isSoldOut = false;

    @Column(name = "score", nullable = false)
    private Double score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flash_sale_campaign_id", nullable = false)
    private FlashSaleCampaign flashSaleCampaign;

    @OneToMany(mappedBy = "flashSaleProduct", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<UserPurchaseLimit> userPurchaseLimits = new ArrayList<>();

    public void addUserPurchaseLimit(UserPurchaseLimit userPurchaseLimit) {
        this.userPurchaseLimits.add(userPurchaseLimit);
        userPurchaseLimit.setFlashSaleProduct(this);
    }

}
