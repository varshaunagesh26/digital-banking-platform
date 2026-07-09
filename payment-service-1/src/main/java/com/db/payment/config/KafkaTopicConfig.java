package com.db.payment.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

public class KafkaTopicConfig {

    @Bean
    public NewTopic paymentEventTopic() {
        return TopicBuilder.name("payment-request-event")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
