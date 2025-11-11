package com.ecommerce.order.repository;

import com.ecommerce.order.entity.Order;
import com.ecommerce.library.enumeration.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

    Optional<Order> findByOrderIdAndUserId(Long orderId, Long userId);
}

