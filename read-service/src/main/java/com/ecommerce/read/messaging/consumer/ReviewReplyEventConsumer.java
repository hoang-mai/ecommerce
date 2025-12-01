package com.ecommerce.read.messaging.consumer;

import com.ecommerce.library.kafka.event.review.CreateReviewReplyEvent;
import com.ecommerce.library.kafka.event.review.UpdateReviewReplyEvent;
import com.ecommerce.library.kafka.event.review.DeleteReviewReplyEvent;
import com.ecommerce.read.service.ReviewViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.*;

@Service
@RequiredArgsConstructor
public class ReviewReplyEventConsumer {

    private final ReviewViewService reviewViewService;

    @KafkaListener(topics = CREATE_REVIEW_REPLY_TOPIC, groupId = READ_SERVICE_GROUP)
    public void listenCreate(CreateReviewReplyEvent event){
        reviewViewService.createReviewReply(event);
    }

    @KafkaListener(topics = UPDATE_REVIEW_REPLY_TOPIC, groupId = READ_SERVICE_GROUP)
    public void listenUpdate(UpdateReviewReplyEvent event){
        reviewViewService.updateReviewReply(event);
    }

    @KafkaListener(topics = DELETE_REVIEW_REPLY_TOPIC, groupId = READ_SERVICE_GROUP)
    public void listenDelete(DeleteReviewReplyEvent event){
        reviewViewService.deleteReviewReply(event);
    }
}

