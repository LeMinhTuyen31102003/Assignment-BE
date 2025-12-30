package com.example.quizz.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

@Schema(description = "Quiz request data")
public record QuizRequestDTO(
        @Schema(description = "Quiz title", example = "Java Basics Quiz", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Quiz title is required")
        @Size(max = 150, message = "Title must not exceed 150 characters")
        String title,

        @Schema(description = "Quiz description", example = "Test your Java knowledge")
        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,

        @Schema(description = "Duration in minutes", example = "30", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Duration is required")
        @Min(value = 1, message = "Duration must be at least 1 minute")
        Integer durationMinutes,

        @Schema(description = "List of question IDs to include in quiz")
        List<UUID> questionIds
) {
}
