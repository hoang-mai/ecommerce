package com.ecommerce.read.entity;

import com.ecommerce.library.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product_attribute_values")
public class ProductAttributeValue extends BaseEntity {
    @Id
    @Column(name = "attribute_value_id")
    private Long attributeValueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_attribute_id", nullable = false)
    private ProductAttribute productAttribute;

    @Column(name = "value", nullable = false)
    private String value;

    @OneToMany(mappedBy = "productAttributeValue", cascade = CascadeType.ALL)
    private List<ProductVariantAttributeValue> productVariantAttributeValues;
}
