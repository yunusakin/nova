package org.novacore.order.messaging;

import org.novacore.lib.events.OrderCreatedEvent;
import org.novacore.lib.kafka.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderCreated(OrderCreatedEvent event) {
        UUID key = event.orderId();
        kafkaTemplate.send(KafkaTopics.ORDER_CREATED, key.toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish order created event for {}", key, ex);
                    } else {
                        log.info("Order created event published for {} at partition {} offset {}", key, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                    }
                });
    }

    public void publishOrderCreated(UUID orderId, UUID userId, UUID productId, int quantity,
                                    java.math.BigDecimal productPrice, java.math.BigDecimal totalPrice) {
        publishOrderCreated(new OrderCreatedEvent(orderId, userId, productId, quantity, productPrice, totalPrice, Instant.now()));
    }
}
