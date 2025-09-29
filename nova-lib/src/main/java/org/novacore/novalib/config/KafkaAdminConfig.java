package org.novacore.novalib.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaAdminConfig {

    public static final String USER_CREATED_TOPIC = "user-created";
    public static final String PRODUCT_CREATED_TOPIC = "product-created";
    public static final String ORDER_CREATED_TOPIC = "order-created";

    /**
     * User events topic
     */
    @Bean
    public NewTopic userCreatedTopic() {
        return new NewTopic(USER_CREATED_TOPIC, 1, (short) 1);
    }

    /**
     * Product events topic
     */
    @Bean
    public NewTopic productCreatedTopic() {
        return new NewTopic(PRODUCT_CREATED_TOPIC, 1, (short) 1);
    }

    /**
     * Order events topic
     */
    @Bean
    public NewTopic orderCreatedTopic() {
        return new NewTopic(ORDER_CREATED_TOPIC, 1, (short) 1);
    }
}
