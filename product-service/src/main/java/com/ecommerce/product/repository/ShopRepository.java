package com.ecommerce.product.repository;

import com.ecommerce.library.enumeration.ShopStatus;
import com.ecommerce.product.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepository extends JpaRepository<Shop,Long> {

    @Query("""
            
            SELECT COUNT(s) FROM Shop s
                        WHERE s.ownerId = :currentUserId AND (s.shopStatus = 'ACTIVE' OR s.shopStatus = 'SUSPENDED')
            """)
    long countByOwnerIdAndStatus(Long currentUserId);

    @Query("""
            SELECT s FROM Shop s
            WHERE (:status IS NULL OR s.shopStatus = :status)
            AND (:keyword IS NULL OR LOWER(s.shopName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(s.province) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(s.ward) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Shop> searchShops(@Param("status") ShopStatus status,
                           @Param("keyword") String keyword,
                          Pageable pageable);

    @Query("""
            SELECT s FROM Shop s
            WHERE s.ownerId = :ownerId
            AND (:status IS NULL OR s.shopStatus = :status)
            AND (:keyword IS NULL OR LOWER(s.shopName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(s.province) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(s.ward) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Shop> searchShopsByOwner(@Param("ownerId") Long ownerId,
                                   @Param("status") ShopStatus status,
                                   @Param("keyword") String keyword,
                                   Pageable pageable);
}
