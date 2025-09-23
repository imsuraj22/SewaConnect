package com.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic userDeleteApproveTopic() {
        return TopicBuilder.name("user-delete-approved")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic userDeleteRejectTopic() {
        return TopicBuilder.name("user-delete-rejected")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic claimRequestTopic() {
        return TopicBuilder.name("approve-claim-request")
                .partitions(1)
                .replicas(1)
                .build();
    }

}
