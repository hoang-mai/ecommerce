package com.ecommerce.read.service;

import com.ecommerce.library.kafka.event.review.CreateReviewViewEvent;
import com.ecommerce.library.kafka.event.review.UpdateReviewViewEvent;
import com.ecommerce.library.kafka.event.review.DeleteReviewViewEvent;
import com.ecommerce.library.kafka.event.review.CreateReviewReplyEvent;
import com.ecommerce.library.kafka.event.review.UpdateReviewReplyEvent;
import com.ecommerce.library.kafka.event.review.DeleteReviewReplyEvent;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.entity.ReviewView;

public interface ReviewViewService {
    void createReviewView(CreateReviewViewEvent createReviewViewEvent);
    void updateReviewView(UpdateReviewViewEvent updateReviewViewEvent);
    void deleteReviewView(DeleteReviewViewEvent deleteReviewViewEvent);
    void createReviewReply(CreateReviewReplyEvent createReviewReplyEvent);
    void updateReviewReply(UpdateReviewReplyEvent updateReviewReplyEvent);
    void deleteReviewReply(DeleteReviewReplyEvent deleteReviewReplyEvent);

    PageResponse<ReviewView> getReviewsByProductId(Long productId, String stars, Boolean isOwner, Long shopId, Boolean isReply, int pageNo, int pageSize, String sortBy, String sortDir);

    ReviewView getReviewByOrderItemId(Long orderItemId);
}
