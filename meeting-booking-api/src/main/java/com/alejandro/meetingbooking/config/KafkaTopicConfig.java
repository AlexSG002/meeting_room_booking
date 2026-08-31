package com.alejandro.meetingbooking.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String RESERVATION_CREATED_TOPIC = "reservation-created";

    @Bean
    public NewTopic reservationCreatedTopic() {

        return TopicBuilder.name(RESERVATION_CREATED_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
