package com.example.quizz.service;

import com.example.quizz.dto.request.ExamSubmissionRequestDTO;
import com.example.quizz.dto.response.ExamResultResponseDTO;
import com.example.quizz.dto.response.QuizSubmissionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ExamService {
    ExamResultResponseDTO submitExam(UUID userId, UUID quizId, ExamSubmissionRequestDTO submissionDTO);
    QuizSubmissionResponseDTO getSubmissionById(UUID submissionId);
    Page<QuizSubmissionResponseDTO> getUserSubmissions(UUID userId, Pageable pageable);
    Page<QuizSubmissionResponseDTO> getQuizSubmissions(UUID quizId, Pageable pageable);
}
