package com.mr_n.questionservice.service;

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
        return repo.findAll();
    }

    public List<Question> getQuestionsByCategory(String type) {
        return repo.findByCategory(type);
    }

    public Question addQuestion(Question question) {
        return repo.save(question);
    }
}
