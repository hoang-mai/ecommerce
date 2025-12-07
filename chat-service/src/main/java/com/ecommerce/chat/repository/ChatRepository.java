package com.ecommerce.chat.repository;

import com.ecommerce.chat.entity.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {

    Optional<Chat> findByUserCacheList__idAndShopCache__id(String useCacheId, String shopId);
}
