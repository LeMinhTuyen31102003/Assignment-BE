package com.example.quizz.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record ExamSubmissionRequestDTO(
        @NotEmpty(message = "Answers are required")
        @Valid
        List<QuestionAnswerDTO> answers
) {
    public record QuestionAnswerDTO(
            @NotNull(message = "Question ID is required")
            UUID questionId,

            @NotEmpty(message = "Selected answers are required")
            List<UUID> selectedAnswerIds
    ) {
    }
}
