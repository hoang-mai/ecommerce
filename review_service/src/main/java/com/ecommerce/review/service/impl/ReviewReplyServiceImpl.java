package com.ecommerce.review.service.impl;

import com.ecommerce.review.dto.ReqReviewReplyDTO;
import com.ecommerce.review.entity.Review;
import com.ecommerce.review.entity.ReviewReply;
import com.ecommerce.review.messaging.producer.ReviewReplyEventProducer;
import com.ecommerce.review.repository.ReviewReplyRepository;
import com.ecommerce.review.repository.ReviewRepository;
import com.ecommerce.review.service.ReviewReplyService;
import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.review.CreateReviewReplyEvent;
import com.ecommerce.library.kafka.event.review.DeleteReviewReplyEvent;
import com.ecommerce.library.kafka.event.review.UpdateReviewReplyEvent;
import com.ecommerce.library.utils.MessageError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReviewReplyServiceImpl implements ReviewReplyService {
    private final ReviewReplyRepository reviewReplyRepository;
    private final ReviewRepository reviewRepository;
    private final UserHelper userHelper;
    private final ReviewReplyEventProducer reviewReplyEventProducer;

    @Override
    public void createReply(ReqReviewReplyDTO reqReviewReplyDTO) {
        Long replierId = userHelper.getCurrentUserId();
        Review review = reviewRepository.findById(reqReviewReplyDTO.getReviewId())
                .orElseThrow(() -> new NotFoundException(MessageError.REVIEW_NOT_FOUND));
        ReviewReply reply = ReviewReply.builder()
                .review(review)
                .replierId(replierId)
                .content(reqReviewReplyDTO.getContent())
                .build();
        ReviewReply saved = reviewReplyRepository.save(reply);
        reviewReplyEventProducer.sendCreate(CreateReviewReplyEvent.builder()
                .replyId(saved.getReviewReplyId())
                .reviewId(review.getReviewId())
                .replierId(replierId)
                .content(saved.getContent())
                .createdAt(LocalDateTime.now())
                .build()
        );
    }

    @Override
    public void updateReply(Long replyId, ReqReviewReplyDTO reqReviewReplyDTO) {
        Long replierId = userHelper.getCurrentUserId();
        ReviewReply reply = reviewReplyRepository.findByReviewReplyIdAndReplierId(replyId, replierId)
                .orElseThrow(() -> new NotFoundException(MessageError.REVIEW_REPLY_NOT_FOUND));
        reply.setContent(reqReviewReplyDTO.getContent());
        ReviewReply saved = reviewReplyRepository.save(reply);
        reviewReplyEventProducer.sendUpdate(UpdateReviewReplyEvent.builder()
                .replyId(saved.getReviewReplyId())
                .reviewId(saved.getReview().getReviewId())
                .replierId(saved.getReplierId())
                .content(saved.getContent())
                .updatedAt(LocalDateTime.now())
                .build()
        );
    }

    @Override
    public void deleteReply(Long replyId) {
        reviewReplyRepository.findById(replyId).ifPresent(reply -> reviewReplyEventProducer.sendDelete(
                DeleteReviewReplyEvent.builder()
                        .replyId(reply.getReviewReplyId())
                        .reviewId(reply.getReview().getReviewId())
                        .build()
        ));
        reviewReplyRepository.deleteById(replyId);
    }

}
