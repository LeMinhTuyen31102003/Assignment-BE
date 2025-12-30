package com.example.quizz.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record QuizResponseDTO(
        UUID id,
        String title,
        String description,
        Integer durationMinutes,
        Integer totalQuestions,
        List<QuestionSummaryDTO> questions,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public record QuestionSummaryDTO(
            UUID id,
            String content,
            Integer score
    ) {
    }
}
