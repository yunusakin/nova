package org.novacore.product.messaging;

import org.novacore.lib.events.ProductCreatedEvent;
import org.novacore.lib.kafka.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class ProductEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ProductEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ProductEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishProductCreated(ProductCreatedEvent event) {
        UUID key = event.productId();
        kafkaTemplate.send(KafkaTopics.PRODUCT_CREATED, key.toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish product created event for {}", key, ex);
                    } else {
                        log.info("Product created event published for {} at partition {} offset {}", key, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                    }
                });
    }

    public void publishProductCreated(UUID productId, String name, java.math.BigDecimal price, int stock) {
        publishProductCreated(new ProductCreatedEvent(productId, name, price, stock, Instant.now()));
    }
}
