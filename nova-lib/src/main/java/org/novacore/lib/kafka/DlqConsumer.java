package org.novacore.lib.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DlqConsumer {

    private static final Logger log = LoggerFactory.getLogger(DlqConsumer.class);

    @KafkaListener(topics = {KafkaTopics.USER_CREATED + ".DLT", KafkaTopics.PRODUCT_CREATED + ".DLT", KafkaTopics.ORDER_CREATED + ".DLT"}, groupId = "nova-dlq-listener")
    public void listen(ConsumerRecord<String, Object> record) {
        log.error("DLQ message received for topic {} partition {} offset {}: {}", record.topic(), record.partition(), record.offset(), record.value());
    }
}
