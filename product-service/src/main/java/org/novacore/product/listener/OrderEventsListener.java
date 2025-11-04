package org.novacore.product.listener;

import org.novacore.lib.events.OrderCreatedEvent;
import org.novacore.lib.kafka.KafkaTopics;
import org.novacore.product.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderEventsListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventsListener.class);

    private final ProductRepository productRepository;

    public OrderEventsListener(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    @KafkaListener(topics = KafkaTopics.ORDER_CREATED, groupId = "product-service")
    public void onOrderCreated(OrderCreatedEvent event) {
        productRepository.findById(event.productId()).ifPresentOrElse(product -> {
            int remaining = product.getStock() - event.quantity();
            if (remaining < 0) {
                log.warn("Order {} would reduce stock below zero for product {}", event.orderId(), event.productId());
                return;
            }
            product.setStock(remaining);
            log.info("Product {} stock reduced to {} due to order {}", product.getId(), remaining, event.orderId());
        }, () -> log.warn("Product {} not found when processing order {}", event.productId(), event.orderId()));
    }
}
