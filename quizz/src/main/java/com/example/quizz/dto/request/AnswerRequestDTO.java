package com.example.quizz.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Answer request data")
public record AnswerRequestDTO(
        @Schema(description = "Answer content", example = "A programming language", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Answer content is required")
        String content,

        @Schema(description = "Is this the correct answer", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "isCorrect flag is required")
        Boolean isCorrect
) {
}
