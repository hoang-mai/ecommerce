package com.ecommerce.flash.sale.repository;

import com.ecommerce.flash.sale.entity.FlashSaleProduct;
import com.ecommerce.flash.sale.entity.UserPurchaseLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPurchaseLimitRepository extends JpaRepository<UserPurchaseLimit, Long> {

    Optional<UserPurchaseLimit> findByUserIdAndFlashSaleProduct(Long userId, FlashSaleProduct flashSaleProduct);
}

