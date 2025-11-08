package com.ecommerce.inventory.entity;

import com.ecommerce.library.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_variant_id", nullable = false)
    private Long productVariantId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity = 0;

}

