package com.ecommerce.order.repository;

import com.ecommerce.order.entity.ProductCartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCartItemRepository extends JpaRepository<ProductCartItem, Long> {
}
