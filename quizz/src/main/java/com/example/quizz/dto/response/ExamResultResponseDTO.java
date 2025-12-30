package com.example.quizz.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record ExamResultResponseDTO(
        UUID submissionId,
        String quizTitle,
        Double earnedScore,
        Double totalScore,
        Double percentage,
        Integer totalQuestions,
        Integer correctAnswers,
        Boolean passed,
        List<QuestionResultDTO> questionResults,
        LocalDateTime submissionTime
) {
    public record QuestionResultDTO(
            UUID questionId,
            String questionContent,
            Double questionScore,
            List<UUID> submittedAnswerIds,
            Set<UUID> correctAnswerIds,
            Boolean isCorrect
    ) {
    }
}
