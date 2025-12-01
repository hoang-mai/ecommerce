package com.ecommerce.read.repository;

import com.ecommerce.read.entity.ReviewView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewViewRepository extends MongoRepository<ReviewView, String> {
}

