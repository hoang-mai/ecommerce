package com.ecommerce.chat.notification.repository;

import com.ecommerce.chat.notification.entity.ShopCache;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopCacheRepository extends MongoRepository<ShopCache, String> {
}

