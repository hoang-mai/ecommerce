package com.commerce.review.messaging.producer;

import com.ecommerce.library.kafka.event.review.CreateReviewReplyEvent;
import com.ecommerce.library.kafka.event.review.UpdateReviewReplyEvent;
import com.ecommerce.library.kafka.event.review.DeleteReviewReplyEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.*;

@Service
@RequiredArgsConstructor
public class ReviewReplyEventProducer {
    private final KafkaTemplate<Long, CreateReviewReplyEvent> createKafkaTemplate;
    private final KafkaTemplate<Long, UpdateReviewReplyEvent> updateKafkaTemplate;
    private final KafkaTemplate<Long, DeleteReviewReplyEvent> deleteKafkaTemplate;

    public void sendCreate(CreateReviewReplyEvent event) {
        createKafkaTemplate.send(CREATE_REVIEW_REPLY_TOPIC, event.getReplyId(), event);
    }

    public void sendUpdate(UpdateReviewReplyEvent event) {
        updateKafkaTemplate.send(UPDATE_REVIEW_REPLY_TOPIC, event.getReplyId(), event);
    }

    public void sendDelete(DeleteReviewReplyEvent event) {
        deleteKafkaTemplate.send(DELETE_REVIEW_REPLY_TOPIC, event.getReplyId(), event);
    }
}
