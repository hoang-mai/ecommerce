package com.ecommerce.flash.sale.entity;

import com.ecommerce.library.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_purchase_limits")
public class UserPurchaseLimit extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "user_purchase_limit_id")
    private Long userPurchaseLimitId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flash_sale_product_id", nullable = false)
    private FlashSaleProduct flashSaleProduct;

    @Column(name = "purchased_quantity", nullable = false)
    private Integer purchasedQuantity;
}
