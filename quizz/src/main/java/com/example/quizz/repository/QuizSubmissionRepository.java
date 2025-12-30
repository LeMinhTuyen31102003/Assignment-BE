package com.example.quizz.repository;

import com.example.quizz.entity.QuizSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, UUID> {

    @EntityGraph(attributePaths = {"user", "quiz"})
    Page<QuizSubmission> findByUserId(UUID userId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "quiz"})
    Page<QuizSubmission> findByQuizId(UUID quizId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "quiz"})
    List<QuizSubmission> findByQuizIdOrderBySubmissionTimeDesc(UUID quizId);
}
