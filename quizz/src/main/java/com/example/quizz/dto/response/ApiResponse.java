package com.example.quizz.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Standard API response wrapper")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        @Schema(description = "Request success status", example = "true")
        boolean success,

        @Schema(description = "Response message", example = "Operation completed successfully")
        String message,

        @Schema(description = "Response data")
        T data,

        @Schema(description = "Timestamp", example = "2025-12-30T10:30:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp
) {
    public ApiResponse(boolean success, String message, T data) {
        this(success, message, data, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
