package com.example.edu.school.user.repository;

import com.example.edu.school.user.dto.response.UserPreviewResponse;
import com.example.edu.school.user.dto.response.information.ParentResponse;
import com.example.edu.school.user.dto.response.information.StudentResponse;
import com.example.edu.school.user.model.Parent;
import com.example.edu.school.user.model.Role;
import com.example.edu.school.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<Parent> findParentByFirstNameAndMiddleNameAndLastNameAndPhoneNumber(
            String firstName, String middleName, String lastName, String phoneNumber);

    Optional<User> findByEmail(String email);

    @Query("""
                SELECT new com.example.edu.school.user.dto.response.information.ParentResponse(
                    p.userId,
                    p.firstName,
                    p.middleName,
                    p.lastName,
                    p.phoneNumber,
                    p.email,
                    p.role,
                    p.gender,
                    p.dateOfBirth,
                    p.codeNumber,
                    p.address,
                    p.avatarUrl,
                    p.createdAt,
                    p.updatedAt,
                    ps.parentRelationship
                )
                FROM Parent p
                JOIN ParentStudent ps ON p.userId = ps.parent.userId
                WHERE ps.student.userId = :studentId
            """)
    Set<ParentResponse> findParentByStudentId(@Param("studentId") Long studentId);

    @Query("""
            SELECT new com.example.edu.school.user.dto.response.information.StudentResponse(
                s.userId,
                s.firstName,
                s.middleName,
                s.lastName,
                s.phoneNumber,
                s.email,
                s.role,
                s.gender,
                s.dateOfBirth,
                s.codeNumber,
                s.address,
                s.avatarUrl,
                s.createdAt,
                s.updatedAt,
                ps.parentRelationship
            )
            FROM Student s
            LEFT JOIN ParentStudent ps ON s.userId = ps.student.userId
            WHERE ps.parent.userId = :parentId
            """)
    Set<StudentResponse> findStudentByParentId(@Param("parentId") Long parentId);

    @Query("""
                 SELECT new com.example.edu.school.user.dto.response.UserPreviewResponse(
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
    Page<UserPreviewResponse> searchUsers(String query, PageRequest pageable);

    @Query("""
                 SELECT new com.example.edu.school.user.dto.response.UserPreviewResponse(
                     u.userId,
                     u.firstName,
                     u.middleName,
                     u.lastName,
                     u.email,
                     u.avatarUrl
                 )
                 FROM User u
                     WHERE :role IS NULL OR u.role = :role
                 """)
    Page<UserPreviewResponse> getUsers(Role role, PageRequest pageable);
}