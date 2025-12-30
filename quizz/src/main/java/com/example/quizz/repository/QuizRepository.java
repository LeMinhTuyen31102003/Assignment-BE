package com.example.quizz.repository;

import com.example.quizz.entity.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, UUID> {

    @EntityGraph(attributePaths = {"questions", "questions.answers"})
    Optional<Quiz> findById(UUID id);

    @EntityGraph(attributePaths = {"questions"})
    Page<Quiz> findAllByIsActive(Boolean isActive, Pageable pageable);
}
