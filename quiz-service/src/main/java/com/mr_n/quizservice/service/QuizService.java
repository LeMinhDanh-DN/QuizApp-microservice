package com.mr_n.quizservice.service;

import com.mr_n.quizservice.exception.ResourceNotFoundException;
import com.mr_n.quizservice.feign.QuestionClient;
import com.mr_n.quizservice.kafka.QuizEventProducer;
import com.mr_n.quizservice.model.QuestionWrapper;
import com.mr_n.quizservice.model.Quiz;
import com.mr_n.quizservice.model.QuizResult;
import com.mr_n.quizservice.model.Response;
import com.mr_n.quizservice.model.event.QuizSubmittedEvent;
import com.mr_n.quizservice.repo.QuizRepo;
import com.mr_n.quizservice.repo.QuizResultRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuizService {

    @Autowired
    private QuizRepo quizRepo;

    @Autowired
    private QuizResultRepo quizResultRepo;

    @Autowired
    private QuestionClient questionClient;

    @Autowired
    private QuizEventProducer quizEventProducer;

    public Quiz createQuiz(String category, int numQ, String title) {

        List<Integer> questionIds = questionClient.getQuestionForQuiz(category, numQ).getBody();

        if (questionIds == null || questionIds.isEmpty()) {
            throw new ResourceNotFoundException("No questions found for category: " + category);
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setCategory(category);
        quiz.setQuestionIds(questionIds);

        return quizRepo.save(quiz);
    }

    public List<QuestionWrapper> getQuizQuestions(Integer id) {
        Quiz quiz = quizRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + id));

        List<QuestionWrapper> questions = questionClient.getQuestionsByIds(quiz.getQuestionIds()).getBody();

        if (questions == null || questions.isEmpty()) {
            throw new ResourceNotFoundException("No questions found for the provided IDs");
        }

        return questions;
    }

    public Integer getResult(List<Response> res) {
        return questionClient.getScore(res).getBody();
    }

    public void submitQuizAsync(Integer quizId, List<Response> res, Integer userId) {
        QuizSubmittedEvent event = new QuizSubmittedEvent(quizId, res, userId, res.size(), LocalDateTime.now());
        quizEventProducer.sendQuizSubmission(event);
    }

    public List<QuizResult> getUserQuizHistory(Long userId) {
        return quizResultRepo.findByUserIdOrderBySubmittedAtDesc(userId);
    }

    public QuizResult getQuizResultForUser(Integer quizId, Long userId) {
        return quizResultRepo.findByQuizIdAndUserId(quizId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No result found for quizId: " + quizId + " and userId: " + userId));
    }
}
