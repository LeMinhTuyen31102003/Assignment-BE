package com.example.quizz.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AnswerRequestDTO(
        @NotBlank(message = "Answer content is required")
        String content,

        @NotNull(message = "isCorrect flag is required")
        Boolean isCorrect
) {
}
