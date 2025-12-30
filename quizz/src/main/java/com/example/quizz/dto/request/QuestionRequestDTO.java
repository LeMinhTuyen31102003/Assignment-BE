package com.example.quizz.dto.request;

import com.example.quizz.enums.QuestionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Question request data")
public record QuestionRequestDTO(
        @Schema(description = "Question content", example = "What is Java?", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Question content is required")
        String content,

        @Schema(description = "Question type", example = "SINGLE_CHOICE", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Question type is required")
        QuestionType type,

        @Schema(description = "Score points", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Score is required")
        @Min(value = 1, message = "Score must be at least 1")
        Integer score,

        @Schema(description = "List of answer options", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "At least one answer is required")
        @Valid
        List<AnswerRequestDTO> answers
) {
}
