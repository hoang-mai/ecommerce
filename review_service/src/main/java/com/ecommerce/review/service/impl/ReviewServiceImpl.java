package com.ecommerce.review.service.impl;

import com.ecommerce.review.dto.ReqReviewDTO;
import com.ecommerce.review.entity.ProductCache;
import com.ecommerce.review.entity.Review;
import com.ecommerce.review.messaging.producer.ReviewEventProducer;
import com.ecommerce.review.repository.OrderItemCacheRepository;
import com.ecommerce.review.repository.ProductCacheRepository;
import com.ecommerce.review.repository.ReviewRepository;
import com.ecommerce.review.service.FileService;
import com.ecommerce.review.service.ReviewService;
import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.review.CreateReviewViewEvent;
import com.ecommerce.library.kafka.event.review.DeleteReviewViewEvent;
import com.ecommerce.library.kafka.event.review.UpdateReviewViewEvent;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserHelper userHelper;
    private final FileService fileService;
    private final OrderItemCacheRepository orderItemCacheRepository;
    private final ProductCacheRepository productCacheRepository;
    private final ReviewEventProducer reviewEventProducer;

    @Override
    public void createReview(ReqReviewDTO reqReviewDTO, List<MultipartFile> imageUrls) {

        ProductCache productCache = productCacheRepository.findById(reqReviewDTO.getProductId())
                .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));
        if(!productCache.getProductVariantIds().contains(reqReviewDTO.getProductVariantId())){
            throw new NotFoundException(MessageError.PRODUCT_VARIANT_NOT_FOUND);
        }

        if(!orderItemCacheRepository.existsByOrderItemIdAndUserId(
                reqReviewDTO.getOrderItemId(),
                userHelper.getCurrentUserId())) {
            throw new NotFoundException(MessageError.ORDER_ITEM_NOT_FOUND);
        }

        Long userId = userHelper.getCurrentUserId();
        Review review = reviewRepository.save(
                Review.builder()
                        .ratingNumber(reqReviewDTO.getRating())
                        .comment(reqReviewDTO.getComment())
                        .orderItemId(reqReviewDTO.getOrderItemId())
                        .productId(reqReviewDTO.getProductId())
                        .productVariantId(reqReviewDTO.getProductVariantId())
                        .userId(userId)
                        .attributes(reqReviewDTO.getAttributes())
                        .build()
        );
        if(FnCommon.isNotNullOrEmptyList(imageUrls)) {
            List<String> uploadFiles = fileService.uploadFiles(imageUrls, "reviews/" + review.getReviewId());
            review.setImageUrls(uploadFiles);
            reviewRepository.save(review);
        }

        CreateReviewViewEvent event = CreateReviewViewEvent.builder()
                .reviewId(review.getReviewId())
                .orderItemId(review.getOrderItemId())
                .productId(review.getProductId())
                .productVariantId(review.getProductVariantId())
                .userId(review.getUserId())
                .rating(review.getRatingNumber())
                .comment(review.getComment())
                .imageUrls(review.getImageUrls())
                .attributes(review.getAttributes())
                .createdAt(LocalDateTime.now())
                .build();

        reviewEventProducer.send(event);

    }

    @Override
    public void updateReview(Long reviewId, ReqReviewDTO reqReviewDTO, List<MultipartFile> imageUrls) {
        Long userId = userHelper.getCurrentUserId();
        Review review = reviewRepository.findByUserIdAndReviewId(userId, reviewId)
                .orElseThrow(() -> new NotFoundException(MessageError.REVIEW_NOT_FOUND));
        review.setRatingNumber(reqReviewDTO.getRating());
        review.setComment(reqReviewDTO.getComment());
        if(FnCommon.isNotNullOrEmptyList(reqReviewDTO.getDeletedImageUrls())){
            reqReviewDTO.getDeletedImageUrls().forEach(url -> {
                fileService.deleteFile(url);
                review.deleteImageUrl(url);
            });
        }
        if(FnCommon.isNotNullOrEmptyList(imageUrls)) {
            List<String> uploadFiles = fileService.uploadFiles(imageUrls, "reviews/" + review.getReviewId());
            review.addImageUrls(uploadFiles);
        }
        reviewRepository.save(review);

        UpdateReviewViewEvent updateEvent = UpdateReviewViewEvent.builder()
                .reviewId(review.getReviewId())
                .rating(review.getRatingNumber())
                .comment(review.getComment())
                .imageUrls(review.getImageUrls())
                .attributes(review.getAttributes())
                .build();
        reviewEventProducer.sendUpdate(updateEvent);
    }


    @Override
    public void deleteReview(Long reviewId) {
        Long userId = userHelper.getCurrentUserId();
        Review review = reviewRepository.findByUserIdAndReviewId(userId, reviewId)
                .orElseThrow(() -> new NotFoundException(MessageError.REVIEW_NOT_FOUND));
        if(FnCommon.isNotNullOrEmptyList(review.getImageUrls())) {
            review.getImageUrls().forEach(fileService::deleteFile);
        }
        reviewRepository.delete(review);
        DeleteReviewViewEvent deleteEvent = DeleteReviewViewEvent.builder()
                .reviewId(review.getReviewId())
                .build();
        reviewEventProducer.sendDelete(deleteEvent);
    }
}
