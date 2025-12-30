package com.example.quizz.controller;

import com.example.quizz.dto.request.QuizRequestDTO;
import com.example.quizz.dto.response.QuizResponseDTO;
import com.example.quizz.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/quizzes")
@RequiredArgsConstructor
@Tag(name = "Quiz Management", description = "APIs for managing quizzes")
public class QuizController {

    private final QuizService quizService;

    @PostMapping
    @Operation(summary = "Create a new quiz", description = "Creates a new quiz with optional questions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Quiz created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<QuizResponseDTO> createQuiz(@Valid @RequestBody QuizRequestDTO requestDTO) {
        QuizResponseDTO response = quizService.createQuiz(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get quiz by ID", description = "Retrieves a quiz with all its questions and answers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quiz found"),
            @ApiResponse(responseCode = "404", description = "Quiz not found")
    })
    public ResponseEntity<QuizResponseDTO> getQuizById(
            @Parameter(description = "Quiz ID") @PathVariable UUID id) {
        QuizResponseDTO response = quizService.getQuizById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all quizzes", description = "Retrieves all active quizzes with pagination and sorting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quizzes retrieved successfully")
    })
    public ResponseEntity<Page<QuizResponseDTO>> getAllQuizzes(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sort,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<QuizResponseDTO> response = quizService.getAllQuizzes(pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update quiz", description = "Updates an existing quiz's information and questions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quiz updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Quiz not found")
    })
    public ResponseEntity<QuizResponseDTO> updateQuiz(
            @Parameter(description = "Quiz ID") @PathVariable UUID id,
            @Valid @RequestBody QuizRequestDTO requestDTO) {
        QuizResponseDTO response = quizService.updateQuiz(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete quiz", description = "Soft deletes a quiz (sets isActive to false)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Quiz deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Quiz not found")
    })
    public ResponseEntity<Void> deleteQuiz(
            @Parameter(description = "Quiz ID") @PathVariable UUID id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{quizId}/questions/{questionId}")
    @Operation(summary = "Add question to quiz", description = "Adds an existing question to a quiz")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question added successfully"),
            @ApiResponse(responseCode = "404", description = "Quiz or question not found")
    })
    public ResponseEntity<QuizResponseDTO> addQuestionToQuiz(
            @Parameter(description = "Quiz ID") @PathVariable UUID quizId,
            @Parameter(description = "Question ID") @PathVariable UUID questionId) {
        QuizResponseDTO response = quizService.addQuestionToQuiz(quizId, questionId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{quizId}/questions/{questionId}")
    @Operation(summary = "Remove question from quiz", description = "Removes a question from a quiz")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question removed successfully"),
            @ApiResponse(responseCode = "404", description = "Quiz or question not found")
    })
    public ResponseEntity<QuizResponseDTO> removeQuestionFromQuiz(
            @Parameter(description = "Quiz ID") @PathVariable UUID quizId,
            @Parameter(description = "Question ID") @PathVariable UUID questionId) {
        QuizResponseDTO response = quizService.removeQuestionFromQuiz(quizId, questionId);
        return ResponseEntity.ok(response);
    }
}
