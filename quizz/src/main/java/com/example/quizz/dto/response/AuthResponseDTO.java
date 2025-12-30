package com.example.quizz.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication response with JWT token")
public record AuthResponseDTO(
        @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken,
        
        @Schema(description = "JWT refresh token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String refreshToken,
        
        @Schema(description = "Token type", example = "Bearer")
        String tokenType,
        
        @Schema(description = "User data")
        UserResponseDTO user
) {
    public AuthResponseDTO(String accessToken, String refreshToken, UserResponseDTO user) {
        this(accessToken, refreshToken, "Bearer", user);
    }
}

