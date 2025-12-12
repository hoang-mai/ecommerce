package com.ecommerce.review.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_item_caches")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemCache {
    @Id
    @Column(name = "order_item_id")
    private Long orderItemId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_variant_id")
    private Long productVariantId;

    @Column(name = "user_id")
    private Long userId;
}

