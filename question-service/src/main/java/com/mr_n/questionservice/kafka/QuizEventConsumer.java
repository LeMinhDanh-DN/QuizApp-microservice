package com.mr_n.questionservice.kafka;

import com.mr_n.questionservice.event.QuizSubmittedEvent;
import com.mr_n.questionservice.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class QuizEventConsumer {

    @Autowired
    private QuestionService questionService;

    @KafkaListener(topics = "quiz-submissions-topic", groupId = "question-group")
    public void consumeQuizSubmission(QuizSubmittedEvent event) {
        System.out.println("Received Quiz Submission Event for Quiz ID: " + event.getQuizId());

        Integer score = questionService.calculateScore(event.getResponses());

        System.out.println("Calculated Score for Quiz ID " + event.getQuizId() + " is: " + score);

        questionService.responseQuizScore(event.getQuizId(), score);

        System.out.println("Result was sent sucessfully!");
    }
}
