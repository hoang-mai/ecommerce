package com.ecommerce.order.repository;

import com.ecommerce.order.entity.ProductCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCacheRepository extends JpaRepository<ProductCache, Long> {
}
