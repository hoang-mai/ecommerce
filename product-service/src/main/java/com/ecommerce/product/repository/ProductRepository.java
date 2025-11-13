package com.ecommerce.product.repository;

import com.ecommerce.library.enumeration.ProductVariantStatus;
import com.ecommerce.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
            SELECT p FROM Product p
            LEFT JOIN FETCH p.category
            LEFT JOIN FETCH p.productImages
            LEFT JOIN FETCH p.productAttributes pa
            LEFT JOIN FETCH pa.productAttributeValues
            LEFT JOIN FETCH p.productVariants pv
            LEFT JOIN FETCH pv.productVariantAttributeValues
            WHERE p.productId = :productId
            """)
    Optional<Product> findByIdWithDetails(@Param("productId") Long productId);


    @Query("""
            SELECT p FROM Product p WHERE
            (:shopId IS NULL OR p.shopId = :shopId) AND
            (:categoryId IS NULL OR p.category.categoryId = :categoryId) AND
            (:status IS NULL OR p.productStatus = :status) AND
            (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
             LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Product> searchProducts(@Param("shopId") Long shopId,
                                 @Param("categoryId") Long categoryId,
                                 @Param("status") ProductVariantStatus status,
                                 @Param("keyword") String keyword,
                                 Pageable pageable);

}

