package com.mr_n.questionservice.service;

import com.mr_n.questionservice.exception.ResourceNotFoundException;
import com.mr_n.questionservice.model.Question;
import com.mr_n.questionservice.repo.QuestionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private QuestionRepo repo;

    public List<com.mr_n.questionservice.model.Question> getAllQuestions() {
        List<Question> questions = repo.findAll();

        if (questions.isEmpty()) {
            throw new ResourceNotFoundException("No questions found in database");
        }
        return questions;
    }

    public List<Question> getQuestionsByCategory(String type) {
        List<Question> questions = repo.findByCategory(type);

        if (questions.isEmpty()) {
            throw new ResourceNotFoundException("No questions found for category: " + type);
        }
        return questions;
    }

    public Question addQuestion(Question question) {
        return repo.save(question);
    }

    public List<Integer> getQuestionForQuiz(String category, Integer numQ) {
        List<Integer> questionIds = repo.findRandomQuestionsByCategory(category, numQ);

        if(questionIds .isEmpty()){
            throw new ResourceNotFoundException("No questions found for category: " + category);
        }

        return questionIds;
    }
}
