package com.ecommerce.review.controller;

import com.ecommerce.review.dto.ReqReviewDTO;
import com.ecommerce.review.service.ReviewService;
import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = Constant.REVIEW)
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final MessageService messageService;


    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<BaseResponse<Void>> createReview(
            @RequestPart(value = "imageUrls") List<MultipartFile> imageUrls,
            @Valid @RequestPart ReqReviewDTO reqReviewDTO
    ) {
        reviewService.createReview(reqReviewDTO, imageUrls);
        return ResponseEntity.ok(
                BaseResponse.<Void>builder()
                        .statusCode(201)
                        .message(messageService.getMessage(MessageSuccess.REVIEW_CREATED_SUCCESS))
                        .build()
        );
    }

    @PatchMapping(value = "/{reviewId}", consumes = "multipart/form-data")
    public ResponseEntity<BaseResponse<Void>> updateReview(
            @PathVariable Long reviewId,
            @RequestPart ReqReviewDTO reqReviewDTO,
            @RequestPart(value = "imageUrls") List<MultipartFile> imageUrls
            ) {
        reviewService.updateReview(reviewId, reqReviewDTO, imageUrls);
        return ResponseEntity.ok(
                BaseResponse.<Void>builder()
                        .statusCode(200)
                        .message(messageService.getMessage(MessageSuccess.REVIEW_UPDATED_SUCCESS))
                        .build()
        );
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<BaseResponse<Void>> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(
                BaseResponse.<Void>builder()
                        .statusCode(200)
                        .message(messageService.getMessage(MessageSuccess.REVIEW_DELETED_SUCCESS))
                        .build()
        );
    }
}

