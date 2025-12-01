package com.ecommerce.review.repository;

import com.ecommerce.review.entity.ReviewReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewReplyRepository extends JpaRepository<ReviewReply, Long> {

    Optional<ReviewReply> findByReviewReplyIdAndReplierId(Long reviewReplyId, Long replierId);
}

