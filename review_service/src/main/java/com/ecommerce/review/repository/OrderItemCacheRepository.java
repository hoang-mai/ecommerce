package com.ecommerce.review.repository;

import com.ecommerce.review.entity.OrderItemCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemCacheRepository extends JpaRepository<OrderItemCache, Long> {
    boolean existsByOrderItemIdAndUserId(Long orderItemId, Long userId);
}

