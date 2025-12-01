package com.ecommerce.read.repository;

import com.ecommerce.library.enumeration.ShopStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.ecommerce.read.entity.ShopView;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopViewRepository extends MongoRepository<ShopView,String> {
    Optional<ShopView> findBy_idAndOwnerId(String _id, String ownerId);

    Optional<ShopView> findBy_idAndShopStatus(String _id, ShopStatus shopStatus);

    boolean existsBy_idAndOwnerId(String id, String ownerId);
}
