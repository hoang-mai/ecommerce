package com.ecommerce.review.service;

import com.ecommerce.review.dto.ReqReviewDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewService {
    void createReview(ReqReviewDTO reqReviewDTO, List<MultipartFile> imageUrls);
    void updateReview(Long reviewId, ReqReviewDTO reqReviewDTO, List<MultipartFile> imageUrls);
    void deleteReview(Long reviewId);
}

