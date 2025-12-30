package com.example.quizz.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Standard error response")
public record ErrorResponse(
        @Schema(description = "Timestamp when error occurred", example = "2025-12-30T10:30:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp,

        @Schema(description = "HTTP status code", example = "404")
        int status,

        @Schema(description = "Error type", example = "Not Found")
        String error,

        @Schema(description = "Error message", example = "Question not found with id: xxx")
        String message,

        @Schema(description = "Request path", example = "/api/v1/questions/xxx")
        String path
) {
    public ErrorResponse(int status, String error, String message, String path) {
        this(LocalDateTime.now(), status, error, message, path);
    }
}
