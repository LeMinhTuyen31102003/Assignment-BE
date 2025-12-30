package com.example.quizz.dto.request;

import com.example.quizz.validation.StrongPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "User request data")
public record UserRequestDTO(
        @Schema(description = "User email address", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @Schema(description = "User password (min 8 characters, must be strong)", example = "Pass@123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        @StrongPassword(message = "Password must contain at least one uppercase, lowercase, digit and special character")
        String password,

        @Schema(description = "User full name", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Full name is required")
        @Size(max = 100, message = "Full name must not exceed 100 characters")
        String fullName
) {
}
