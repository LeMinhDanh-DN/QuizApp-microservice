package com.mr_n.quizservice.service;
import com.mr_n.quizservice.exception.ResourceNotFoundException;
import com.mr_n.quizservice.feign.QuizInterface;
import com.mr_n.quizservice.model.QuestionWrapper;
import com.mr_n.quizservice.model.Quiz;
import com.mr_n.quizservice.model.Response;
import com.mr_n.quizservice.repo.QuizRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizService {

    @Autowired
    private QuizRepo quizDao;

    @Autowired
    private QuizInterface quizInterface;

    public Quiz createQuiz(String category, int numQ, String title) {

        List<Integer> questionIds = quizInterface.getQuestionForQuiz(category, numQ).getBody();

        if (questionIds.isEmpty()) {
            throw new ResourceNotFoundException("No questions found for category: " + category);
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setCategory(category);
        quiz.setQuestionIds(questionIds);

        return quizDao.save(quiz);
    }

    public List<QuestionWrapper> getQuizQuestions(Integer id) {
        Quiz quiz = quizDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + id));

        List<QuestionWrapper> questions = quizInterface.getQuestionsByIds(quiz.getQuestionIds()).getBody();

        if (questions == null || questions.isEmpty()) {
            throw new ResourceNotFoundException("No questions found for the provided IDs");
        }

        return questions;
    }

    public Integer getResult (List<Response> res){
            return quizInterface.getScore(res).getBody();
    }
}

