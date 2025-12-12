package com.ecommerce.review.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @ElementCollection
    @CollectionTable(name = "product_cache_variant_ids", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "variant_id")
    private List<Long> productVariantIds;
}

