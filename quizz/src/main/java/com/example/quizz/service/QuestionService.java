package com.example.quizz.service;

import com.example.quizz.dto.request.QuestionRequestDTO;
import com.example.quizz.dto.response.QuestionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface QuestionService {
    QuestionResponseDTO createQuestion(QuestionRequestDTO requestDTO);
    Page<QuestionResponseDTO> getAllQuestions(Pageable pageable);
    QuestionResponseDTO getQuestionById(UUID id);
    QuestionResponseDTO updateQuestion(UUID id, QuestionRequestDTO requestDTO);
    void deleteQuestion(UUID id);
}
