package com.ecommerce.order.repository;

import com.ecommerce.order.entity.ShopCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopCacheRepository extends JpaRepository<ShopCache, Long> {
}
