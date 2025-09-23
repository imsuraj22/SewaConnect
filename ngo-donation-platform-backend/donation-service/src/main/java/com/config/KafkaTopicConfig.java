package com.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic createClaimRequestTopic() {
        return TopicBuilder.name("create-claim-request")
                .partitions(1)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic claimApprovedTopic() {
        return TopicBuilder.name("claim-request-approved")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
