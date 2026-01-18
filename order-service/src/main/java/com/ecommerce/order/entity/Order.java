package com.ecommerce.order.entity;

import com.ecommerce.library.entity.BaseEntity;
import com.ecommerce.library.enumeration.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "order_id", updatable = false, nullable = false)
    private Long orderId;

    @Column(name = "order_code", nullable = false)
    private String orderCode;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "cart_item_id")
    private Long cartItemId;

    @Column(name= "note", columnDefinition = "TEXT")
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @Column(name = "reason")
    private String reason;

    @Column(name = "total_price", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "receiver_name", nullable = false)
    private String receiverName;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    public void addOrderItem(OrderItem orderItem) {
        items.add(orderItem);
        orderItem.setOrder(this);
    }

    public void addTotalPrice(BigDecimal price) {
        if (this.totalPrice == null) {
            this.totalPrice = BigDecimal.ZERO;
        }
        this.totalPrice = this.totalPrice.add(price);
    }

}

