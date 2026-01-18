package com.ecommerce.payment.entity;

import com.ecommerce.library.entity.BaseEntity;
import com.ecommerce.library.enumeration.PaymentStatus;
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
@Table(name = "payments")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "payment_id", updatable = false, nullable = false)
    private Long paymentId;

    @Column(name = "payment_code", nullable = false, unique = true)
    private String paymentCode;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name="transaction_no")
    private String transactionNo;

    @Column(name = "payDate")
    private String payDate;

    @Column(name = "reason")
    private String reason;

    @Column(name = "count_refund")
    @Builder.Default
    private Integer countRefund = 0;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderCache> orderCaches = new ArrayList<>();

    public void addPrice(BigDecimal amount) {
        if (this.price == null) {
            this.price = BigDecimal.ZERO;
        }
        this.price = this.price.add(amount);
    }
    public void addOrderCache(OrderCache orderCache) {
        this.orderCaches.add(orderCache);
        orderCache.setPayment(this);
    }
}
