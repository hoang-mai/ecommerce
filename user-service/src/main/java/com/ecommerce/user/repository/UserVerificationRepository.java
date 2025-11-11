package com.ecommerce.user.repository;

import com.ecommerce.user.entity.User;
import com.ecommerce.user.entity.UserVerification;
import com.ecommerce.library.enumeration.UserVerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserVerificationRepository extends JpaRepository<UserVerification, Long> {
    boolean existsByVerificationCode(String verificationCode);

    @Query("""
            SELECT uv FROM UserVerification uv LEFT JOIN uv.user u WHERE
            (:status IS NULL OR uv.userVerificationStatus = :status) AND
            (:keyword IS NULL OR LOWER(uv.verificationCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
             LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
             LOWER(u.middleName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
             LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
             LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
             LOWER(uv.accountNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
             LOWER(uv.bankName) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<UserVerification> searchUserVerifications(@Param("status") UserVerificationStatus status,
                                                   @Param("keyword") String keyword,
                                                   Pageable pageable);


    boolean existsByUserAndUserVerificationStatusNot(User user, UserVerificationStatus userVerificationStatus);

    /**
     * Tìm yêu cầu xác minh của người dùng theo userId với phân trang và filter
     *
     * @param userId ID của người dùng
     * @param status Trạng thái yêu cầu (nullable)
     * @param keyword Từ khóa tìm kiếm (nullable)
     * @param pageable Thông tin phân trang
     * @return Page chứa danh sách UserVerification của người dùng
     */
    @Query("""
            SELECT uv FROM UserVerification uv LEFT JOIN uv.user u WHERE u.userId = :userId AND
            (:status IS NULL OR uv.userVerificationStatus = :status) AND
            (:keyword IS NULL OR LOWER(uv.verificationCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
             LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
             LOWER(u.middleName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
             LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
             LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
             LOWER(uv.accountNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
             LOWER(uv.bankName) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<UserVerification> findByUserId(@Param("userId") Long userId,
                                       @Param("status") UserVerificationStatus status,
                                       @Param("keyword") String keyword,
                                       Pageable pageable);
}
