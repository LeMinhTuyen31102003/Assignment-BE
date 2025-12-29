package com.example.quizz.dto.request;

import com.example.quizz.enums.QuestionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record QuestionRequestDTO(
        @NotBlank(message = "Question content is required")
        String content,

        @NotNull(message = "Question type is required")
        QuestionType type,

        @NotNull(message = "Score is required")
        @Min(value = 1, message = "Score must be at least 1")
        Integer score,

        @NotEmpty(message = "At least one answer is required")
        @Valid
        List<AnswerRequestDTO> answers
) {
}
