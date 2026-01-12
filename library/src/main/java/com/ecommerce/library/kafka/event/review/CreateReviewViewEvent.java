package com.ecommerce.library.kafka.event.review;

import com.ecommerce.library.enumeration.RatingNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateReviewViewEvent {
    private Long reviewId;
    private Long orderItemId;
    private Long productId;
    private Long productVariantId;
    private Long userId;
    private Long ownerId;
    private Long shopId;
    private String productName;
    private RatingNumber rating;
    private String comment;
    private List<String> imageUrls;
    private Map<String, String> attributes;
    private Instant createdAt;
}
