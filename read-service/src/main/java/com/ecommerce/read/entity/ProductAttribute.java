package com.ecommerce.read.entity;


import com.ecommerce.library.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product_attributes")
public class ProductAttribute extends BaseEntity {

    @Id
    @Column(name = "attribute_id")
    private Long attributeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "attribute_name", nullable = false)
    private String attributeName;

    @OneToMany(mappedBy = "productAttribute", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductAttributeValue> productAttributeValues = new ArrayList<>();

    public void addAttributeValue(ProductAttributeValue value) {
        productAttributeValues.add(value);
        value.setProductAttribute(this);
    }
}
