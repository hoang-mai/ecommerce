package com.ecommerce.chat.repository;

import com.ecommerce.chat.entity.PushSubscription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PushSubscriptionRepository extends MongoRepository<PushSubscription, String> {

    /**
     * Tìm PushSubscription theo endpoint
     */
    Optional<PushSubscription> findByEndpoint(String endpoint);

    /**
     * Tìm tất cả PushSubscription của một user
     */
    List<PushSubscription> findByUserId(Long userId);


    /**
     * Kiểm tra xem subscription đã tồn tại chưa
     */
    boolean existsByEndpointAndUserId(String endpoint, Long userId);

    List<PushSubscription> findPushSubscriptionByUserIdAndActive(Long userId, boolean active);
}

