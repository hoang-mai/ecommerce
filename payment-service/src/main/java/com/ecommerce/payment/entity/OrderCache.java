package com.ecommerce.payment.entity;

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
@Table(name = "order_caches")
public class OrderCache extends BaseEntity {
    @Id
    private Long orderId;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "total_price", nullable = false)
    @Builder.Default
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @OneToMany(mappedBy = "orderCache", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItemCache> orderItems = new ArrayList<>();

    public void addOrderItemCache(OrderItemCache orderItemCache) {
        this.orderItems.add(orderItemCache);
        orderItemCache.setOrderCache(this);
    }
}
