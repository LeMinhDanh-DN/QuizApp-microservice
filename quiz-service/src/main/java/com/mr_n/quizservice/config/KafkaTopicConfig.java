package com.mr_n.quizservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String QUIZ_SUBMISSIONS_TOPIC = "quiz-submissions-topic";

    @Bean
    public NewTopic quizSubmissionsTopic() {
        return TopicBuilder.name(QUIZ_SUBMISSIONS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
