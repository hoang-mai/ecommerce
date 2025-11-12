package com.ecommerce.auth.producer;

import com.ecommerce.library.kafka.event.user.UpdateAccountStatusEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.UPDATE_ACCOUNT_STATUS_TOPIC;

@Service
@RequiredArgsConstructor
public class UserEventProducer {
    private final KafkaTemplate<Long, UpdateAccountStatusEvent> kafkaTemplate;

    public void send(UpdateAccountStatusEvent updateAccountStatusEvent) {
        kafkaTemplate.send(UPDATE_ACCOUNT_STATUS_TOPIC, updateAccountStatusEvent.getUserId(), updateAccountStatusEvent);
    }
}
