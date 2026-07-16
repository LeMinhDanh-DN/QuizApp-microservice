package com.mr_n.questionservice.controller;

import com.mr_n.questionservice.exception.ResourceNotFoundException;
import com.mr_n.questionservice.model.Question;
import com.mr_n.questionservice.model.QuestionWrapper;
import com.mr_n.questionservice.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    public QuestionService questionService;


    @GetMapping("/all")
    public ResponseEntity<List<Question>> getAllQuestions() {
        List<Question> questions = questionService.getAllQuestions();
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    @GetMapping("/category/{type}")
    public ResponseEntity<List<Question>> getQuestionsByCategory(@PathVariable String type) {
        List<Question> questions = questionService.getQuestionsByCategory(type);
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Question> addQuestion(@RequestBody Question question) {
        return new ResponseEntity<>(questionService.addQuestion(question), HttpStatus.CREATED);
    }

    @GetMapping("/generate")
    public ResponseEntity<List<Integer>> getQuestionForQuiz (@RequestParam String category,
                                                             @RequestParam Integer numQ){

        List<Integer> questionIds = questionService.getQuestionForQuiz(category, numQ);
        return new ResponseEntity<>(questionIds, HttpStatus.OK);
    }

    @PostMapping("getQuestion")
    public ResponseEntity<List<QuestionWrapper>> getQuestionsByIds(@RequestBody List<Integer> questionIds){

        List<QuestionWrapper> questions = questionService.getQuestionsByIds(questionIds);
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }
}


