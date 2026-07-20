package com.mr_n.quizservice.repo;

import com.mr_n.quizservice.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepo extends JpaRepository<com.mr_n.quizservice.model.Quiz, Integer> {
}
