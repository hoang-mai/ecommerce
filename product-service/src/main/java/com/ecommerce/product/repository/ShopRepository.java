package com.ecommerce.product.repository;

import com.ecommerce.library.enumeration.ShopStatus;
import com.ecommerce.product.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop,Long> {

    @Query("""
            
            SELECT COUNT(s) FROM Shop s
                        WHERE s.ownerId = :currentUserId AND (s.shopStatus = 'ACTIVE' OR s.shopStatus = 'SUSPENDED')
            """)
    long countByOwnerIdAndStatus(Long currentUserId);

    Optional<Shop> findByShopIdAndOwnerId(Long shopId, Long ownerId);
}
