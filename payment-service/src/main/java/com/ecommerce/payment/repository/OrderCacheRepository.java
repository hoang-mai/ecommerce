package com.ecommerce.payment.repository;

import com.ecommerce.payment.entity.OrderCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OrderCacheRepository extends JpaRepository<OrderCache, Long> {
    @Query("""
            SELECT oc FROM OrderCache oc
            LEFT JOIN FETCH oc.orderItems oic
            LEFT JOIN FETCH oc.payment p
            WHERE oc.orderId = :orderId
            """)
    Optional<OrderCache> findByOrderId(Long orderId);
}
