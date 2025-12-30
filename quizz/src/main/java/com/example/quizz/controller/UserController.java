package com.example.quizz.controller;

import com.example.quizz.dto.request.UserRequestDTO;
import com.example.quizz.dto.response.UserResponseDTO;
import com.example.quizz.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * User Management Controller (ADMIN ONLY)
 * All endpoints require ADMIN role
 * For public registration, use /api/v1/auth/register
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management (ADMIN)", description = "CRUD APIs for managing users - ADMIN ONLY. For self-registration use /api/v1/auth/register")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Create a new user (ADMIN ONLY)", 
        description = "Admin creates a new user with USER role. For self-registration, use POST /api/v1/auth/register instead."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or email already exists"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO requestDTO) {
        log.info("Admin creating new user: {}", requestDTO.email());
        UserResponseDTO response = userService.createUser(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user by ID (ADMIN ONLY)", description = "Retrieves a user by their unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "User ID") @PathVariable UUID id) {
        log.info("Admin retrieving user by ID: {}", id);
        UserResponseDTO response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users (ADMIN ONLY)", description = "Retrieves all active users with pagination and sorting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sort,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String direction) {
        
        log.info("Admin retrieving all users - page: {}, size: {}", page, size);
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<UserResponseDTO> response = userService.getAllUsers(pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user (ADMIN ONLY)", description = "Updates an existing user's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "User ID") @PathVariable UUID id,
            @Valid @RequestBody UserRequestDTO requestDTO) {
        log.info("Admin updating user: {}", id);
        UserResponseDTO response = userService.updateUser(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user (ADMIN ONLY)", description = "Soft deletes a user (sets isActive to false)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID") @PathVariable UUID id) {
        log.info("Admin deleting user: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
