package org.novacore.userservice.events;

import lombok.RequiredArgsConstructor;
import org.novacore.novalib.events.UserCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserEventPublisher {

    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;

    private static final String TOPIC = "user-created";

    public void publishUserCreated(UserCreatedEvent event) {
        kafkaTemplate.send(TOPIC, event.userId().toString(), event);
    }
}

