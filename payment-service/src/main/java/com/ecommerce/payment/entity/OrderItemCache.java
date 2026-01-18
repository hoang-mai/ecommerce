package com.ecommerce.payment.entity;

import com.ecommerce.library.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_item_caches")
public class OrderItemCache extends BaseEntity {
    @Id
    @Column(name = "order_item_id", updatable = false, nullable = false)
    private Long orderItemId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_variant_id", nullable = false)
    private Long productVariantId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderCache orderCache;

    @Column(name = "quantity_discount")
    @Builder.Default
    private Integer quantityDiscount = 0;

    @Column(name = "discount")
    @Builder.Default
    private Double discount = 0.0;

    @Column(name = "price", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "total_price", nullable = false)
    @Builder.Default
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Column(name = "total_discount", nullable = false)
    @Builder.Default
    private BigDecimal totalDiscount = BigDecimal.ZERO;

    @Column(name = "total_final_price", nullable = false)
    @Builder.Default
    private BigDecimal totalFinalPrice = BigDecimal.ZERO;

    @Column(name = "flash_sale_product_id")
    private Long flashSaleProductId;
}
