package com.ecommerce.read.repository;

import com.ecommerce.read.entity.ReviewView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewViewRepository extends MongoRepository<ReviewView, String> {
    Optional<ReviewView> findByOrderItemId(String orderItemId);
}

