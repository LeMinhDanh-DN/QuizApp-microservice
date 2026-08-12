package com.mr_n.quizservice.kafka;

import com.mr_n.quizservice.config.KafkaTopicConfig;
import com.mr_n.quizservice.event.QuizSubmittedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class QuizEventProducer {

    @Autowired
    private KafkaTemplate<String, QuizSubmittedEvent> kafkaTemplate;

    public void sendQuizSubmission(QuizSubmittedEvent event) {
        kafkaTemplate.send(KafkaTopicConfig.QUIZ_SUBMISSIONS_TOPIC, String.valueOf(event.getQuizId()), event);
        System.out.println("Sent QuizSubmittedEvent to Kafka topic: " + KafkaTopicConfig.QUIZ_SUBMISSIONS_TOPIC + " for Quiz ID: " + event.getQuizId());
    }
}
