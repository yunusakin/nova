package org.novacore.productservice.events;

import lombok.RequiredArgsConstructor;
import org.novacore.novalib.events.ProductCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductEventPublisher {

    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

    private static final String TOPIC = "product-created";

    public void publishProductCreated(ProductCreatedEvent event) {
        kafkaTemplate.send(TOPIC, event.productId().toString(), event);
    }
}

