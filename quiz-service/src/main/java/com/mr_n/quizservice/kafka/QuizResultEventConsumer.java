package com.mr_n.quizservice.kafka;

import com.mr_n.quizservice.model.QuizResult;
import com.mr_n.quizservice.model.event.QuizResultEvent;
import com.mr_n.quizservice.repo.QuizResultRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class QuizResultEventConsumer {

    @Autowired
    private QuizResultRepo quizResultRepo;

    @KafkaListener(topics = "quiz-result-topic", groupId = "quiz-group")
    public void consumeQuizResult(QuizResultEvent event) {

        QuizResult quizResult = QuizResult.builder()
                .quizId(event.getQuizId())
                .userId(event.getUserId() != null ? Long.valueOf(event.getUserId()) : null)
                .score(event.getScore())
                .totalQuestions(event.getTotalQuestions())
                .submittedAt(event.getSubmittedAt())
                .build();

        quizResultRepo.save(quizResult);
    }
}
