package com.ecommerce.read.repository;

import com.ecommerce.library.enumeration.OrderStatus;
import com.ecommerce.read.entity.OrderView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderViewRepository extends MongoRepository<OrderView, String> {

}
