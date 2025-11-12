package com.ecommerce.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "product_caches")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCache {
    @Id
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_variant_ids")
    private Set<Long> productVariantIds;
}
