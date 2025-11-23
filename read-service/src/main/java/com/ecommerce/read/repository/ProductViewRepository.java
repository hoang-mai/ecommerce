package com.ecommerce.read.repository;

import com.ecommerce.read.entity.ProductView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductViewRepository extends MongoRepository<ProductView,String> {
}
