package com.example.quizz.controller;

import com.example.quizz.dto.request.ExamSubmissionRequestDTO;
import com.example.quizz.dto.response.ExamResultResponseDTO;
import com.example.quizz.dto.response.QuizSubmissionResponseDTO;
import com.example.quizz.service.ExamService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/exam")
@RequiredArgsConstructor
@Tag(name = "Exam Management", description = "APIs for exam submission and result tracking")
public class ExamController {

    private final ExamService examService;

    @PostMapping("/submit/{userId}/{quizId}")
    @Operation(summary = "Submit exam", description = "Submit answers for a quiz and get results with detailed scoring")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam submitted and graded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid submission data"),
            @ApiResponse(responseCode = "404", description = "User or quiz not found")
    })
    public ResponseEntity<ExamResultResponseDTO> submitExam(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @Parameter(description = "Quiz ID") @PathVariable UUID quizId,
            @Valid @RequestBody ExamSubmissionRequestDTO submissionDTO) {
        ExamResultResponseDTO response = examService.submitExam(userId, quizId, submissionDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/submissions/{submissionId}")
    @Operation(summary = "Get submission by ID", description = "Retrieves a specific exam submission by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Submission found"),
            @ApiResponse(responseCode = "404", description = "Submission not found")
    })
    public ResponseEntity<QuizSubmissionResponseDTO> getSubmissionById(
            @Parameter(description = "Submission ID") @PathVariable UUID submissionId) {
        QuizSubmissionResponseDTO response = examService.getSubmissionById(submissionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/submissions")
    @Operation(summary = "Get user submissions", description = "Retrieves all exam submissions for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Submissions retrieved successfully")
    })
    public ResponseEntity<Page<QuizSubmissionResponseDTO>> getUserSubmissions(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "submissionTime") String sort,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<QuizSubmissionResponseDTO> response = examService.getUserSubmissions(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/quizzes/{quizId}/submissions")
    @Operation(summary = "Get quiz submissions", description = "Retrieves all submissions for a specific quiz")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Submissions retrieved successfully")
    })
    public ResponseEntity<Page<QuizSubmissionResponseDTO>> getQuizSubmissions(
            @Parameter(description = "Quiz ID") @PathVariable UUID quizId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "submissionTime") String sort,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<QuizSubmissionResponseDTO> response = examService.getQuizSubmissions(quizId, pageable);
        return ResponseEntity.ok(response);
    }
}
