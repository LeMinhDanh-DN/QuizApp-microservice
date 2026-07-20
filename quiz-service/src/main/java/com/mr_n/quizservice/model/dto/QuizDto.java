package com.mr_n.quizservice.model.dto;

public record QuizDto(
        String category,
        Integer numQ,
        String title
) {
}
