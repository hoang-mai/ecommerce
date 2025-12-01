package com.ecommerce.library.kafka.event.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReviewViewEvent {
    private Long reviewId;
    private Double rating;
    private String comment;
    private List<String> imageUrls;
    private Map<String, String> attributes;
}

