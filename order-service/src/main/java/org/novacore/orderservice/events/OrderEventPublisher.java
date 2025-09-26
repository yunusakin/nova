package org.novacore.orderservice.events;


import lombok.RequiredArgsConstructor;
import org.novacore.novalib.events.OrderCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    private static final String TOPIC = "order-created";

    public void publishOrderCreated(OrderCreatedEvent event) {
        kafkaTemplate.send(TOPIC, event.orderId().toString(), event);
    }
}

