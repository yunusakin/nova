package org.novacore.novalib.config;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DLQConsumer {

    /**
     * Listens to all Dead Letter Topics (DLT).
     * Example: user-created.DLT, product-created.DLT, order-created.DLT
     */
    @KafkaListener(topicPattern = ".*\\.DLT", groupId = "dlq-monitor")
    public void listenDlq(ConsumerRecord<String, Object> record) {
        log.error("🚨 DLQ received: topic={}, key={}, value={}, headers={}",
                record.topic(), record.key(), record.value(), record.headers());
    }
}

