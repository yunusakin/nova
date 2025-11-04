package org.novacore.order.listener;

import org.novacore.lib.events.ProductCreatedEvent;
import org.novacore.lib.events.UserCreatedEvent;
import org.novacore.lib.kafka.KafkaTopics;
import org.novacore.order.service.ReferenceDataService;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DomainEventLogger {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(DomainEventLogger.class);

    private final ReferenceDataService referenceDataService;

    public DomainEventLogger(ReferenceDataService referenceDataService) {
        this.referenceDataService = referenceDataService;
    }

    @KafkaListener(topics = KafkaTopics.USER_CREATED, groupId = "order-service")
    public void onUserCreated(UserCreatedEvent event) {
        referenceDataService.upsertUser(event);
        log.info("Received user created event: {}", event);
    }

    @KafkaListener(topics = KafkaTopics.PRODUCT_CREATED, groupId = "order-service")
    public void onProductCreated(ProductCreatedEvent event) {
        referenceDataService.upsertProduct(event);
        log.info("Received product created event: {}", event);
    }
}
