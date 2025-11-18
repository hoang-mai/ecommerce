package com.ecommerce.notification.repository;

import com.ecommerce.notification.entity.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {

    /**
     * Tìm PushSubscription theo endpoint
     */
    Optional<PushSubscription> findByEndpoint(String endpoint);

    /**
     * Tìm tất cả PushSubscription của một user
     */
    List<PushSubscription> findByUserId(Long userId);

    /**
     * Tìm tất cả PushSubscription đang active của một user
     */
    @Query("SELECT p FROM PushSubscription p WHERE p.userId = :userId AND p.active = true")
    List<PushSubscription> findActiveByUserId(@Param("userId") Long userId);

    /**
     * Kiểm tra xem subscription đã tồn tại chưa
     */
    boolean existsByEndpointAndUserId(String endpoint, Long userId);
}

