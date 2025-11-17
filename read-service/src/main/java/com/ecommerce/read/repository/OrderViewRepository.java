package com.ecommerce.read.repository;

import com.ecommerce.read.entity.OrderView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderViewRepository extends MongoRepository<OrderView, Long> {
}
