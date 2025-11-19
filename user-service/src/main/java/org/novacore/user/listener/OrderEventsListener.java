package org.novacore.user.listener;

import org.novacore.lib.events.OrderCreatedEvent;
import org.novacore.lib.kafka.KafkaTopics;
import org.novacore.user.service.UserOrderAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventsListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventsListener.class);

    private final UserOrderAuditService auditService;

    public OrderEventsListener(UserOrderAuditService auditService) {
        this.auditService = auditService;
    }

    @KafkaListener(topics = KafkaTopics.ORDER_CREATED, groupId = "user-service-audit")
    public void onOrderCreated(OrderCreatedEvent event) {
        try {
            auditService.record(event);
            log.info("Recorded order {} for user {}", event.orderId(), event.userId());
        } catch (Exception ex) {
            log.error("Failed to record order {} for user {}", event.orderId(), event.userId(), ex);
            throw ex;
        }
    }
}
