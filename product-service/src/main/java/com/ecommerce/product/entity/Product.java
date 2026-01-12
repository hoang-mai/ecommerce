package com.ecommerce.product.entity;

import com.ecommerce.library.entity.BaseEntity;
import com.ecommerce.library.enumeration.ProductStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_status", nullable = false)
    private ProductStatus productStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @ElementCollection
    @CollectionTable(name = "product_details", joinColumns = @JoinColumn(name = "product_id"))
    @MapKeyColumn(name = "detail_key")
    @Column(name = "detail_value")
    @Builder.Default
    private Map<String, String> productDetails = new HashMap<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductImage> productImages = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductAttribute> productAttributes = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductVariant> productVariants = new ArrayList<>();

    public void addProductImage(ProductImage productImage) {
        productImages.add(productImage);
        productImage.setProduct(this);
    }

    public void deleteProductImage(ProductImage productImage) {
        productImages.remove(productImage);
        productImage.setProduct(null);
    }

    public void addProductAttribute(ProductAttribute productAttribute) {
        productAttributes.add(productAttribute);
        productAttribute.setProduct(this);
    }

    public void addProductVariant(ProductVariant productVariant) {
        productVariants.add(productVariant);
        productVariant.setProduct(this);
    }


}
