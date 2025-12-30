package com.example.quizz.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record QuizSubmissionResponseDTO(
        UUID id,
        UUID userId,
        String userEmail,
        UUID quizId,
        String quizTitle,
        Double score,
        Integer totalQuestions,
        Integer correctAnswers,
        Boolean passed,
        LocalDateTime submissionTime
) {
}
