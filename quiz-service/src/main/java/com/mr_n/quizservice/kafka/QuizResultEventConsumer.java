package com.mr_n.quizservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;

import com.mr_n.quizservice.event.QuizResultEvent;

import org.springframework.stereotype.Service;

@Service
public class QuizResultEventConsumer {


    @KafkaListener(topics = "quiz-result-topic", groupId = "quiz-group")
    public void consumeQuizResult(QuizResultEvent event) {

        System.out.println("Received Quiz Result Event for Quiz ID: " + event.getQuizId());

        System.out.println("Total Marks for Quiz ID " + event.getQuizId() + " is: " + event.getTotalMarks());
    }
}
