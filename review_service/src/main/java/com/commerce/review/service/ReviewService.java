package com.commerce.review.service;

import com.commerce.review.dto.ReqReviewDTO;
import com.commerce.review.entity.Review;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewService {
    void createReview(ReqReviewDTO reqReviewDTO, List<MultipartFile> imageUrls);
    void updateReview(Long reviewId, ReqReviewDTO reqReviewDTO, List<MultipartFile> imageUrls);
    void deleteReview(Long reviewId);
}

