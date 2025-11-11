package com.ecommerce.read.repository;

import com.ecommerce.library.enumeration.AccountStatus;
import com.ecommerce.library.enumeration.Role;
import com.ecommerce.read.entity.UserView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserViewRepository extends JpaRepository<UserView, Long> {

    @Query("""
            SELECT u FROM UserView u
            WHERE (:accountStatus IS NULL OR u.accountStatus = :accountStatus)
              AND (:role IS NULL OR u.role = :role)
              AND (:keyword IS NULL OR
                   LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(u.middleName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  )
            """)
    Page<UserView> getUserView(AccountStatus accountStatus, Role role, String keyword, Pageable pageable);
}
