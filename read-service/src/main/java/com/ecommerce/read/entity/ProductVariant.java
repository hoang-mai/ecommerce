package com.ecommerce.read.entity;

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
@Table(name = "product_variants")
public class ProductVariant extends BaseEntity {

    @Id
    @Column(name = "product_variant_id")
    private Long productVariantId;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductVariantAttributeValue> productVariantAttributeValues = new ArrayList<>();

    public void addProductVariantAttributeValue(ProductVariantAttributeValue attributeValue) {
        productVariantAttributeValues.add(attributeValue);
        attributeValue.setProductVariant(this);
    }
}
