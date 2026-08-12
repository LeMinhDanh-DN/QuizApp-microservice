package com.mr_n.questionservice.event;

import com.mr_n.questionservice.model.Response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmittedEvent {
    private Integer quizId;
    private List<Response> responses;
}
