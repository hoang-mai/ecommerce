package com.ecommerce.user.repository;

import com.ecommerce.user.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Address findByUserUserIdAndIsDefault(Long userId, Boolean isDefault);

    List<Address> findByUserUserId(Long userId);
}
