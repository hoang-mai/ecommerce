package com.ecommerce.flash.sale.repository;

import com.ecommerce.flash.sale.entity.FlashSaleProduct;
import com.ecommerce.flash.sale.entity.UserPurchaseLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPurchaseLimitRepository extends JpaRepository<UserPurchaseLimit, Long> {

    @Query("""
        SELECT upl FROM UserPurchaseLimit upl
        JOIN FETCH upl.flashSaleProduct fsp
        WHERE upl.userId = :userId
        AND fsp.productVariantId IN :productVariantIds
        AND fsp = :flashSaleProduct
        """)
    List<UserPurchaseLimit> findByUserIdAndProductVariantIds(
            @Param("userId") Long userId,
            @Param("productVariantIds") List<Long> productVariantIds,
            @Param("flashSaleProduct") FlashSaleProduct FlashSaleProduct);
}

