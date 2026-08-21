package com.mr_n.quizservice.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizResultEvent {
    private Integer quizId;
    private Integer userId;
    private Integer score;
    private Integer totalQuestions;
    private LocalDateTime submittedAt;
}
