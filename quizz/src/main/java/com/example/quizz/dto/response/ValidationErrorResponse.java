package com.example.quizz.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Validation error response with field-level errors")
public record ValidationErrorResponse(
        @Schema(description = "Timestamp when error occurred")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp,

        @Schema(description = "HTTP status code", example = "400")
        int status,

        @Schema(description = "Error type", example = "Validation Failed")
        String error,

        @Schema(description = "General error message", example = "Invalid request parameters")
        String message,

        @Schema(description = "Request path", example = "/api/v1/questions")
        String path,

        @Schema(description = "Field-level validation errors", 
                example = "{\"email\": \"must be a valid email\", \"password\": \"size must be between 8 and 100\"}")
        Map<String, String> errors
) {
    public ValidationErrorResponse(int status, String error, String message, String path, Map<String, String> errors) {
        this(LocalDateTime.now(), status, error, message, path, errors);
    }
}
