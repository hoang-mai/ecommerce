package com.ecommerce.user.repository;

import com.ecommerce.user.dto.ResInfoPreviewUserDTO;
import com.ecommerce.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    @Query("""
            SELECT COUNT(*) > 0
            FROM User u
            WHERE u.userId <> :userId AND u.email = :email
            """)
    boolean existsEmailAlreadyUsedByAnotherUser(Long userId, String email);

    @Query("""
            SELECT new com.ecommerce.user.dto.ResInfoPreviewUserDTO(
                u.userId,
                u.firstName,
                u.middleName,
                u.lastName,
                u.email,
                u.avatarUrl
            )
            FROM User u
            WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(u.middleName) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))
            """)
    Page<ResInfoPreviewUserDTO> searchUsersByNameOrEmail(String query, Pageable pageable);
}