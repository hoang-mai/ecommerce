package com.ecommerce.product.repository;

import com.ecommerce.product.dto.ResCategoryDTO;
import com.ecommerce.product.entity.Category;
import com.ecommerce.library.enumeration.CategoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Override
    @Query("""
                    SELECT c FROM Category c
                    LEFT JOIN FETCH c.parentCategory pc
                    LEFT JOIN FETCH c.subCategories
                    WHERE c.categoryId = :categoryId
            """)
    Optional<Category> findById(Long categoryId);

    Optional<Category> findByCategoryName(String categoryName);


    List<Category> findByParentCategoryCategoryId(Long parentId);

    long countByCategoryIdIn(Set<Long> categoryIds);

    @Query("""
             SELECT c FROM Category c
             WHERE (:keyword IS NULL OR LOWER(c.categoryName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                     LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
             AND (:status IS NULL OR c.categoryStatus = :status)
            """)
    Page<Category> searchCategories(@Param("keyword") String keyword,
                                    @Param("status") CategoryStatus status,
                                    Pageable pageable);

    @Query(value = """
            WITH RECURSIVE CategoryCTE AS (
                SELECT c.category_id, c.parent_id
                FROM categories c
                WHERE c.category_id = :parentCategoryId
                UNION ALL
                SELECT c.category_id, c.parent_id
                FROM categories c
                INNER JOIN CategoryCTE cte ON c.category_id = cte.parent_id
            )
            SELECT IF(COUNT(*) > 0, TRUE, FALSE)
            FROM CategoryCTE
            WHERE category_id = :categoryId;

            """, nativeQuery = true)
    Long isValidParentCategory(Long categoryId, Long parentCategoryId);
}

