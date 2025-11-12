package com.ecommerce.user.messaging.producer;

import com.ecommerce.library.kafka.event.user.UpdateAvatarUserEvent;
import com.ecommerce.library.kafka.event.user.UpdateUserEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.*;

@Service
@RequiredArgsConstructor
public class UserEventProducer {
    private final KafkaTemplate<Long, UpdateUserEvent> kafkaTemplate;
    private final KafkaTemplate<Long, UpdateAvatarUserEvent> kafkaUpdateAvatarUserTemplate;

    public void send(UpdateUserEvent updateUserEvent) {
        kafkaTemplate.send(UPDATE_USER_TOPIC, updateUserEvent.getUserId(), updateUserEvent);
    }

    public void send(UpdateAvatarUserEvent updateAvatarUserEvent) {
        kafkaUpdateAvatarUserTemplate.send(UPDATE_AVATAR_URL_TOPIC, updateAvatarUserEvent.getUserId(), updateAvatarUserEvent);
    }
}
