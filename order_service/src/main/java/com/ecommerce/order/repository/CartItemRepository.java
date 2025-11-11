package com.ecommerce.order.repository;

import com.ecommerce.order.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCart_CartIdAndProductVariantId(Long cartId, Long productVariantId);

    void deleteByCart_CartIdAndProductVariantId(Long cartId, Long productVariantId);
}

