package com.ecommerce.product.repository;

import com.ecommerce.product.entity.ProductVariantAttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariantAttributeValueRepository extends JpaRepository<ProductVariantAttributeValue, Long> {
}
