package com.ecommerce.chat.repository;

import com.ecommerce.chat.entity.ShopCache;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopCacheRepository extends MongoRepository<ShopCache, String> {
}

