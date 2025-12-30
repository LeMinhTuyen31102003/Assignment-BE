package com.example.quizz.controller;

import com.example.quizz.dto.request.LoginRequestDTO;
import com.example.quizz.dto.request.RefreshTokenRequestDTO;
import com.example.quizz.dto.request.UserRequestDTO;
import com.example.quizz.dto.response.AuthResponseDTO;
import com.example.quizz.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 * Handles user registration, login and token refresh with JWT
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "APIs for user authentication with JWT (JSON Web Token)")
public class AuthController {

    private final UserService userService;

    /**
     * Register a new user
     * Password is hashed with BCrypt and JWT tokens are generated
     * 
     * @param requestDTO User registration data
     * @return Authentication response with JWT access token and refresh token
     */
    @PostMapping("/register")
    @Operation(
        summary = "Register new user",
        description = "Create a new user account. Password is hashed with BCrypt. Returns JWT access token (24h) and refresh token (7 days)."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User registered successfully with JWT tokens"),
        @ApiResponse(responseCode = "400", description = "Invalid input data or password too weak"),
        @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody UserRequestDTO requestDTO) {
        log.info("Registration request received for email: {}", requestDTO.email());
        AuthResponseDTO response = userService.register(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * User login
     * Verifies credentials and generates JWT tokens
     * 
     * @param requestDTO Login credentials (email and password)
     * @return Authentication response with JWT tokens
     */
    @PostMapping("/login")
    @Operation(
        summary = "User login",
        description = "Authenticate user with email and password. Password is verified using BCrypt. Returns JWT access token and refresh token."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful with JWT tokens"),
        @ApiResponse(responseCode = "400", description = "Invalid email or password"),
        @ApiResponse(responseCode = "401", description = "Account is deactivated")
    })
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO requestDTO) {
        log.info("Login request received for email: {}", requestDTO.email());
        AuthResponseDTO response = userService.login(requestDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Refresh access token
     * Use refresh token to get new access token without re-login
     * 
     * @param requestDTO Refresh token
     * @return New authentication response with new access token
     */
    @PostMapping("/refresh")
    @Operation(
        summary = "Refresh access token",
        description = "Get new access token using refresh token. Useful when access token expires (24h) but refresh token is still valid (7 days)."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid or expired refresh token"),
        @ApiResponse(responseCode = "401", description = "Account is deactivated")
    })
    public ResponseEntity<AuthResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO requestDTO) {
        log.info("Token refresh request received");
        AuthResponseDTO response = userService.refreshToken(requestDTO);
        return ResponseEntity.ok(response);
    }
}
