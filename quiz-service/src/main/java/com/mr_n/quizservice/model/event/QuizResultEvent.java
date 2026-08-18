package com.mr_n.quizservice.model.event;

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
