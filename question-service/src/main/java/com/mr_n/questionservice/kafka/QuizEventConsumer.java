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
        System.out.println("Received Quiz Submission Event for Quiz ID: " + event.getQuizId() + " from User ID: "
                + event.getUserId());

        Integer score = questionService.calculateScore(event.getResponses());

        questionService.responseQuizScore(event.getQuizId(), event.getUserId(), score, event.getTotalQuestions(),
                event.getSubmittedAt());

        System.out.println("Result was sent successfully!");
    }
}
