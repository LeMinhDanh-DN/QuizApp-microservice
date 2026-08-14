package com.mr_n.questionservice.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.mr_n.questionservice.config.KafkaTopicConfig;
import com.mr_n.questionservice.event.QuizResultEvent;

@Service
public class QuizResultEventProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendQuizResult(QuizResultEvent event) {
        kafkaTemplate.send(KafkaTopicConfig.QUIZ_RESULT_TOPIC, String.valueOf(event.getQuizId()), event);
    }

}
