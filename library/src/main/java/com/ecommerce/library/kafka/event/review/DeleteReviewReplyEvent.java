package com.ecommerce.library.kafka.event.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteReviewReplyEvent {
    private Long replyId;
    private Long reviewId;
}

