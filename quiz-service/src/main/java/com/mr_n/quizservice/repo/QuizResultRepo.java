package com.mr_n.quizservice.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mr_n.quizservice.model.QuizResult;

@Repository
public interface QuizResultRepo extends JpaRepository<QuizResult, Long> {

    Optional<QuizResult> findByQuizIdAndUserId(Integer quizId, Long userId);

    List<QuizResult> findByUserIdOrderBySubmittedAtDesc(Long userId);

}