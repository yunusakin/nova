package org.novacore.order.listener;

import org.novacore.lib.events.ProductCreatedEvent;
import org.novacore.lib.events.UserCreatedEvent;
import org.novacore.lib.kafka.KafkaTopics;
import org.novacore.order.service.ReferenceDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DomainEventLogger {

    private static final Logger log = LoggerFactory.getLogger(DomainEventLogger.class);

    private final ReferenceDataService referenceDataService;

    public DomainEventLogger(ReferenceDataService referenceDataService) {
        this.referenceDataService = referenceDataService;
    }

    @KafkaListener(topics = KafkaTopics.USER_CREATED, groupId = "order-service")
    public void onUserCreated(UserCreatedEvent event) {
        log.debug("Received user created event: {}", event);
        try {
            referenceDataService.upsertUser(event);
        } catch (RuntimeException ex) {
            log.error("Failed to upsert user summary for event {}", event, ex);
            throw ex;
        }
    }

    @KafkaListener(topics = KafkaTopics.PRODUCT_CREATED, groupId = "order-service")
    public void onProductCreated(ProductCreatedEvent event) {
        log.debug("Received product created event: {}", event);
        try {
            referenceDataService.upsertProduct(event);
        } catch (RuntimeException ex) {
            log.error("Failed to upsert product summary for event {}", event, ex);
            throw ex;
        }
    }
}
