package com.ecommerce.review.repository;

import com.ecommerce.review.entity.ProductCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCacheRepository extends JpaRepository<ProductCache, Long> {
}

