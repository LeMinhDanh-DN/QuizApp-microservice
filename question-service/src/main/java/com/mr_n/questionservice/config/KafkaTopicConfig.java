package com.mr_n.questionservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

public class KafkaTopicConfig {
    public static final String QUIZ_RESULT_TOPIC = "quiz-result-topic";

    @Bean
    public NewTopic quizResultTopic() {
        return TopicBuilder.name(QUIZ_RESULT_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
