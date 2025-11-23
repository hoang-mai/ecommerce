package com.ecommerce.read.repository;

import com.ecommerce.read.entity.CartView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartViewRepository extends MongoRepository<CartView,String> {
    Optional<CartView> findByUserId(String currentUserId);
}
