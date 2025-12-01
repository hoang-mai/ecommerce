package com.ecommerce.read.messaging.consumer;

import com.ecommerce.library.kafka.event.review.CreateReviewViewEvent;
import com.ecommerce.library.kafka.event.review.DeleteReviewViewEvent;
import com.ecommerce.library.kafka.event.review.UpdateReviewViewEvent;
import com.ecommerce.read.service.ReviewViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.*;

@Service
@RequiredArgsConstructor
public class ReviewEventConsumer {

    private final ReviewViewService reviewViewService;

    @KafkaListener(topics = CREATE_REVIEW_VIEW_TOPIC, groupId = READ_SERVICE_GROUP)
    public void listenCreate(CreateReviewViewEvent createReviewViewEvent){
        reviewViewService.createReviewView(createReviewViewEvent);
    }

    @KafkaListener(topics = UPDATE_REVIEW_VIEW_TOPIC, groupId = READ_SERVICE_GROUP)
    public void listenUpdate(UpdateReviewViewEvent updateReviewViewEvent){
        reviewViewService.updateReviewView(updateReviewViewEvent);
    }

    @KafkaListener(topics = DELETE_REVIEW_VIEW_TOPIC, groupId = READ_SERVICE_GROUP)
    public void listenDelete(DeleteReviewViewEvent deleteReviewViewEvent){
        reviewViewService.deleteReviewView(deleteReviewViewEvent);
    }
}
