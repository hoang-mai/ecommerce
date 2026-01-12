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
public class UpdateReviewViewEvent {
    private Long reviewId;
    private RatingNumber rating;
    private String comment;
    private List<String> imageUrls;
    private Map<String, String> attributes;
}

