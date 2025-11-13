package com.ecommerce.read.entity;

import com.ecommerce.library.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product_variant_attribute_values")
public class ProductVariantAttributeValue extends BaseEntity {

    @Id
    @Column(name = "product_variant_attribute_value_id")
    private Long productVariantAttributeValueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_attribute_value_id", nullable = false)
    private ProductAttributeValue productAttributeValue;
}
