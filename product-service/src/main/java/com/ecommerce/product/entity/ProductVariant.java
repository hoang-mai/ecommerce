package com.ecommerce.product.entity;

import com.ecommerce.library.entity.BaseEntity;
import com.ecommerce.library.enumeration.ProductVariantStatus;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "product_variant_id")
    private Long productVariantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_variant_status", nullable = false)
    private ProductVariantStatus productVariantStatus;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "sold")
    @Builder.Default
    private Integer sold = 0;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductVariantAttributeValue> productVariantAttributeValues = new ArrayList<>();

    public void addProductVariantAttributeValue(ProductVariantAttributeValue attributeValue) {
        productVariantAttributeValues.add(attributeValue);
        attributeValue.setProductVariant(this);
    }

    public void addSold(Integer quantity) {
        this.sold += quantity;
    }
}
