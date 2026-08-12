package com.mr_n.quizservice.event;

import com.mr_n.quizservice.model.Response;
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
