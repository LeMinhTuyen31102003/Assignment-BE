package com.example.quizz.dto.response;

import com.example.quizz.enums.QuestionType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record QuestionResponseDTO(
        UUID id,
        String content,
        QuestionType type,
        Integer score,
        List<AnswerResponseDTO> answers,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
