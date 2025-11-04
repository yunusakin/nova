package org.novacore.lib.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaAdminConfig {

    public static final int DEFAULT_PARTITIONS = 3;
    public static final short DEFAULT_REPLICATION_FACTOR = 1;

    @Bean
    public NewTopic userCreatedTopic() {
        return TopicBuilder.name(KafkaTopics.USER_CREATED)
                .partitions(DEFAULT_PARTITIONS)
                .replicas(DEFAULT_REPLICATION_FACTOR)
                .build();
    }

    @Bean
    public NewTopic productCreatedTopic() {
        return TopicBuilder.name(KafkaTopics.PRODUCT_CREATED)
                .partitions(DEFAULT_PARTITIONS)
                .replicas(DEFAULT_REPLICATION_FACTOR)
                .build();
    }

    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name(KafkaTopics.ORDER_CREATED)
                .partitions(DEFAULT_PARTITIONS)
                .replicas(DEFAULT_REPLICATION_FACTOR)
                .build();
    }

    @Bean
    public NewTopic userCreatedDlt() {
        return TopicBuilder.name(KafkaTopics.USER_CREATED + ".DLT")
                .partitions(1)
                .replicas(DEFAULT_REPLICATION_FACTOR)
                .build();
    }

    @Bean
    public NewTopic productCreatedDlt() {
        return TopicBuilder.name(KafkaTopics.PRODUCT_CREATED + ".DLT")
                .partitions(1)
                .replicas(DEFAULT_REPLICATION_FACTOR)
                .build();
    }

    @Bean
    public NewTopic orderCreatedDlt() {
        return TopicBuilder.name(KafkaTopics.ORDER_CREATED + ".DLT")
                .partitions(1)
                .replicas(DEFAULT_REPLICATION_FACTOR)
                .build();
    }
}
