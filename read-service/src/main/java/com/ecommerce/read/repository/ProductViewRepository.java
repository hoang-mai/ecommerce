package com.ecommerce.read.repository;

import com.ecommerce.library.enumeration.ProductStatus;
import com.ecommerce.library.enumeration.ShopStatus;
import com.ecommerce.read.entity.ProductView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProductViewRepository extends MongoRepository<ProductView,String> {
    List<ProductView> findByShopId(String shopId);

    Optional<ProductView> findBy_idAndOwnerId(String id, String ownerId);

    Optional<ProductView> findBy_idAndProductStatusAndShopStatus(String _id, ProductStatus productStatus, ShopStatus shopStatus);
}
