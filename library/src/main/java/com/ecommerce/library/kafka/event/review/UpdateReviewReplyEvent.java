package com.ecommerce.library.kafka.event.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReviewReplyEvent {
    private Long replyId;
    private Long reviewId;
    private Long replierId;
    private String content;
    private Instant updatedAt;
}
