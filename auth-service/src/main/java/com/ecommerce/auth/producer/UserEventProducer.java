package com.ecommerce.auth.producer;

import com.ecommerce.library.kafka.event.UpdateAccountStatusEvent;
import com.ecommerce.library.kafka.event.UpdateUserEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.READ_SERVICE_TOPIC;
import static com.ecommerce.library.kafka.Constant.UPDATE_ACCOUNT_STATUS_TOPIC;

@Service
@RequiredArgsConstructor
public class UserEventProducer {
    private final KafkaTemplate<Long, UpdateAccountStatusEvent> kafkaTemplate;

    public void send(UpdateAccountStatusEvent updateAccountStatusEvent) {
        kafkaTemplate.send(UPDATE_ACCOUNT_STATUS_TOPIC, updateAccountStatusEvent.getUserId(), updateAccountStatusEvent);
    }
}
