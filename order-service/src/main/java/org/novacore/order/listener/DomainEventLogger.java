package org.novacore.order.listener;

import org.novacore.lib.events.ProductCreatedEvent;
import org.novacore.lib.events.UserCreatedEvent;
import org.novacore.lib.kafka.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DomainEventLogger {

    private static final Logger log = LoggerFactory.getLogger(DomainEventLogger.class);

    @KafkaListener(topics = KafkaTopics.USER_CREATED, groupId = "order-service")
    public void onUserCreated(UserCreatedEvent event) {
        log.info("Received user created event: {}", event);
    }

    @KafkaListener(topics = KafkaTopics.PRODUCT_CREATED, groupId = "order-service")
    public void onProductCreated(ProductCreatedEvent event) {
        log.info("Received product created event: {}", event);
    }
}
