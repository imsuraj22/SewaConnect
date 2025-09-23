package com.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic ngoStatusTopic() {
        return TopicBuilder.name("ngo-status-events")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic ngoDeleteApproveTopic() {
        return TopicBuilder.name("ngo-delete-approved")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic ngoDeleteRejectTopic() {
        return TopicBuilder.name("ngo-delete-rejected")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
