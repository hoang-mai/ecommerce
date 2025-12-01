package com.commerce.review.repository;

import com.commerce.review.entity.OrderItemCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemCacheRepository extends JpaRepository<OrderItemCache, Long> {
    boolean existsByOrderItemIdAndUserId(Long orderItemId, Long userId);
}

