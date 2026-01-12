package com.ecommerce.read.repository;

import com.ecommerce.read.entity.FlashSaleProductView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface FlashSaleProductViewRepository extends MongoRepository<FlashSaleProductView, String> {

    /**
     * Find all flash sale products by productId where startTime is after the given date
     */
    @Query("{ 'productId': ?0, 'startTime': { $gt: ?1 } }")
    List<FlashSaleProductView> findByProductIdAndStartTimeAfter(String productId, Instant startTime);

    /**
     * Find all ongoing flash sale products by productId
     * Ongoing means: startTime <= currentTime <= endTime
     */
    @Query("{ 'productId': ?0, 'startTime': { $lte: ?1 }, 'endTime': { $gte: ?1 } }")
    List<FlashSaleProductView> findOngoingFlashSalesByProductId(String productId, Instant currentTime);

}
