package com.example.quizz.dto.response;

import java.util.UUID;

public record AnswerResponseDTO(
        UUID id,
        String content,
        Boolean isCorrect
) {
}
