package org.novacore.user.messaging;

import org.novacore.lib.events.UserCreatedEvent;
import org.novacore.lib.kafka.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class UserEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(UserEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public UserEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishUserCreated(UserCreatedEvent event) {
        UUID key = event.userId();
        kafkaTemplate.send(KafkaTopics.USER_CREATED, key.toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish user created event for {}", key, ex);
                    } else {
                        log.info("User created event published for {} at partition {} offset {}", key, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                    }
                });
    }

    public void publishUserCreated(UUID userId, String name, String email) {
        publishUserCreated(new UserCreatedEvent(userId, name, email, Instant.now()));
    }
}
