package com.ecommerce.chat.repository;

import com.ecommerce.chat.entity.UserCache;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCacheRepository extends MongoRepository<UserCache,String> {
}
