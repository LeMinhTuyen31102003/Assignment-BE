package com.example.quizz.service;

import com.example.quizz.dto.request.QuizRequestDTO;
import com.example.quizz.dto.response.QuizResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface QuizService {
    QuizResponseDTO createQuiz(QuizRequestDTO requestDTO);
    QuizResponseDTO getQuizById(UUID id);
    Page<QuizResponseDTO> getAllQuizzes(Pageable pageable);
    QuizResponseDTO updateQuiz(UUID id, QuizRequestDTO requestDTO);
    void deleteQuiz(UUID id);
    QuizResponseDTO addQuestionToQuiz(UUID quizId, UUID questionId);
    QuizResponseDTO removeQuestionFromQuiz(UUID quizId, UUID questionId);
}
