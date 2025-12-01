package com.commerce.review.repository;

import com.commerce.review.entity.ProductCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCacheRepository extends JpaRepository<ProductCache, Long> {
}

