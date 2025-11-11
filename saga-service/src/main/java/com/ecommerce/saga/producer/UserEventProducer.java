package com.ecommerce.saga.producer;

import com.ecommerce.library.kafka.event.CreateUserEvent;
import com.ecommerce.library.kafka.event.UpdateRoleEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.*;

@Service
@RequiredArgsConstructor
public class UserEventProducer {
    private final KafkaTemplate<Long, CreateUserEvent> createUserEventKafkaTemplate;
    private final KafkaTemplate<Long, UpdateRoleEvent> updateRoleEventKafkaTemplate;

    public void send(CreateUserEvent  createUserEvent) {
        createUserEventKafkaTemplate.send(CREATE_USER_TOPIC, createUserEvent.getUserId(), createUserEvent);
    }

    public void send(UpdateRoleEvent updateRoleEvent) {
        updateRoleEventKafkaTemplate.send(UPDATE_ROLE_TOPIC, updateRoleEvent.getUserId(), updateRoleEvent);
    }

}
