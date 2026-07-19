package com.mr_n.quizservice.controller;

import com.mr_n.quizservice.model.QuestionWrapper;
import com.mr_n.quizservice.model.dto.QuizDto;
import com.mr_n.quizservice.model.Quiz;
import com.mr_n.quizservice.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @PostMapping("/create")
    public ResponseEntity<Quiz> createQuiz(@RequestBody QuizDto quizDto) {
        return new ResponseEntity<>(quizService.createQuiz(quizDto.category(), quizDto.numQ(), quizDto.title()), HttpStatus.CREATED);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<List<QuestionWrapper>> getQuizById(@PathVariable Integer id) {
        return new ResponseEntity<>(quizService.getQuizQuestions(id),HttpStatus.OK);
    }
}
