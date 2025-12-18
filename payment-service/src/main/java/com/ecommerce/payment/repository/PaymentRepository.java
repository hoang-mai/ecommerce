package com.ecommerce.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.payment.entity.Payment;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
