package com.commerce.review.messaging.producer;

import com.ecommerce.library.kafka.event.review.CreateReviewViewEvent;
import com.ecommerce.library.kafka.event.review.UpdateReviewViewEvent;
import com.ecommerce.library.kafka.event.review.DeleteReviewViewEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.*;

@Service
@RequiredArgsConstructor
public class ReviewEventProducer {
    private final KafkaTemplate<Long, CreateReviewViewEvent> createKafkaTemplate;
    private final KafkaTemplate<Long, UpdateReviewViewEvent> updateKafkaTemplate;
    private final KafkaTemplate<Long, DeleteReviewViewEvent> deleteKafkaTemplate;

    public void send(CreateReviewViewEvent event) {
        createKafkaTemplate.send(CREATE_REVIEW_VIEW_TOPIC, event.getReviewId(), event);
    }

    public void sendUpdate(UpdateReviewViewEvent event) {
        updateKafkaTemplate.send(UPDATE_REVIEW_VIEW_TOPIC, event.getReviewId(), event);
    }

    public void sendDelete(DeleteReviewViewEvent event) {
        deleteKafkaTemplate.send(DELETE_REVIEW_VIEW_TOPIC, event.getReviewId(), event);
    }
}
