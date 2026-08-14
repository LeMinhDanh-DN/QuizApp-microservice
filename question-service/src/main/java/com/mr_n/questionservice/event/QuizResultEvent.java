package com.mr_n.questionservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultEvent {
    private Integer quizId;
    private Integer totalMarks;
}
