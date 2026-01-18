package com.ecommerce.product.repository;

import com.ecommerce.product.entity.FlashSaleProductCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface FlashSaleProductCacheRepository extends JpaRepository<FlashSaleProductCache, Long> {

    /**
     * Kiểm tra xem product có đang trong flash sale tại thời điểm hiện tại không
     */
    @Query("""
            SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END
            FROM FlashSaleProductCache f
            WHERE f.productId = :productId
            AND f.startTime <= :currentTime
            AND f.endTime >= :currentTime
            """)
    boolean existsByProductIdAndCurrentTimeInFlashSale(
            @Param("productId") Long productId,
            @Param("currentTime") Instant currentTime);

    List<FlashSaleProductCache> findByProductVariantIdAndStartTimeAfter(Long productVariantId, Instant currentTime);
}
