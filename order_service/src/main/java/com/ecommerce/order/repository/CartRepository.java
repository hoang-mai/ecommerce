package com.ecommerce.order.repository;

import com.ecommerce.order.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    @Query("""
            SELECT COUNT(ci)
            FROM Cart c
            JOIN c.cartItems ci
            WHERE c.userId = :userId
            """)
    Integer countCartItemsByUserId(Long userId);
}

