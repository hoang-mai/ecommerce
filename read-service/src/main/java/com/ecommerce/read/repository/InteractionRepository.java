package com.ecommerce.read.repository;

import com.ecommerce.read.entity.Interaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InteractionRepository extends MongoRepository<Interaction, String> {

    // Tìm tất cả tương tác của một user
    List<Interaction> findByUserId(String userId);

    // Tìm tất cả tương tác với một sản phẩm
    Page<Interaction> findByProductId(String productId, Pageable pageable);

    // Tìm tương tác theo user và product
    List<Interaction> findByUserIdAndProductId(String userId, String productId);

    // Tìm tương tác theo user, product và type
    Optional<Interaction> findByUserIdAndProductIdAndInteractionType(
            String userId,
            String productId,
            Interaction.InteractionType interactionType
    );

    // Tìm tương tác theo type
    List<Interaction> findByInteractionType(Interaction.InteractionType interactionType);

    // Tìm tương tác của user theo type
    List<Interaction> findByUserIdAndInteractionType(String userId, Interaction.InteractionType interactionType);

    // Tìm tương tác theo khoảng thời gian
    List<Interaction> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Tìm tương tác của product theo type
    List<Interaction> findByProductIdAndInteractionType(String productId, Interaction.InteractionType interactionType);

    // Đếm số lượng tương tác của một sản phẩm theo type
    Long countByProductIdAndInteractionType(String productId, Interaction.InteractionType interactionType);

    // Đếm số lượng tương tác của user
    Long countByUserId(String userId);

    // Kiểm tra xem user đã tương tác với product chưa
    boolean existsByUserIdAndProductIdAndInteractionType(
            String userId,
            String productId,
            Interaction.InteractionType interactionType
    );
}

