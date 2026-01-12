package com.ecommerce.flash.sale.repository;

import com.ecommerce.flash.sale.entity.FlashSaleProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlashSaleProductRepository extends JpaRepository<FlashSaleProduct, Long> {

    Optional<FlashSaleProduct> findByFlashSaleProductId(Long flashSaleProductId);

    @Query("""
        SELECT f FROM FlashSaleProduct f
        WHERE f.flashSaleCampaign.flashSaleCampaignId = :campaignId
        AND (:ownerId IS NULL OR f.ownerId = :ownerId)
        """)
    Page<FlashSaleProduct> findByCampaignIdOrderByScoreDesc(@Param("campaignId") Long campaignId,
                                                            @Param("ownerId") Long ownerId,
                                                            Pageable pageable);

    @Query("SELECT f FROM FlashSaleProduct f WHERE f.flashSaleCampaign.flashSaleCampaignId = :campaignId " +
           "AND f.productId = :productId AND f.productVariantId = :productVariantId")
    Optional<FlashSaleProduct> findByCampaignAndProductVariant(
            @Param("campaignId") Long campaignId,
            @Param("productId") Long productId,
            @Param("productVariantId") Long productVariantId);

    boolean existsByFlashSaleCampaign_FlashSaleCampaignIdAndProductIdAndProductVariantId(
            Long campaignId, Long productId, Long productVariantId);

    List<FlashSaleProduct> findByProductVariantIdIn(List<Long> productVariantIds);

    @Query("""
        SELECT f FROM FlashSaleProduct f
        JOIN f.flashSaleCampaign c
        WHERE f.productVariantId IN :productVariantIds
        AND c.startTime <= :now
        AND c.endTime >= :now
        """)
    List<FlashSaleProduct> findActiveByProductVariantIdIn(
            @Param("productVariantIds") List<Long> productVariantIds,
            @Param("now") Instant now);
}

