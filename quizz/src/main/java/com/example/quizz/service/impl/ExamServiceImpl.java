package com.example.quizz.service.impl;

import com.example.quizz.dto.request.ExamSubmissionRequestDTO;
import com.example.quizz.dto.response.ExamResultResponseDTO;
import com.example.quizz.dto.response.QuizSubmissionResponseDTO;
import com.example.quizz.entity.Answer;
import com.example.quizz.entity.Question;
import com.example.quizz.entity.Quiz;
import com.example.quizz.entity.QuizSubmission;
import com.example.quizz.entity.User;
import com.example.quizz.enums.QuestionType;
import com.example.quizz.exception.ResourceNotFoundException;
import com.example.quizz.mapper.QuizSubmissionMapper;
import com.example.quizz.repository.QuizRepository;
import com.example.quizz.repository.QuizSubmissionRepository;
import com.example.quizz.repository.UserRepository;
import com.example.quizz.service.ExamService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final QuizSubmissionRepository submissionRepository;
    private final QuizSubmissionMapper submissionMapper;

    private static final double PASS_THRESHOLD = 70.0; // 70% to pass

    @Override
    @Transactional
    public ExamResultResponseDTO submitExam(UUID userId, UUID quizId, ExamSubmissionRequestDTO submissionDTO) {
        // Validate user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Validate quiz
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        if (Boolean.FALSE.equals(quiz.getIsActive())) {
            throw new IllegalStateException("Quiz is not active");
        }

        // Calculate score
        int totalQuestions = quiz.getQuestions().size();
        int correctAnswers = 0;
        double totalScore = 0.0;
        double earnedScore = 0.0;

        Map<UUID, ExamSubmissionRequestDTO.QuestionAnswerDTO> submittedAnswers = 
                submissionDTO.answers().stream()
                        .collect(Collectors.toMap(
                                ExamSubmissionRequestDTO.QuestionAnswerDTO::questionId,
                                answer -> answer
                        ));

        List<ExamResultResponseDTO.QuestionResultDTO> questionResults = new ArrayList<>();

        for (Question question : quiz.getQuestions()) {
            totalScore += question.getScore();
            
            ExamSubmissionRequestDTO.QuestionAnswerDTO submittedAnswer = 
                    submittedAnswers.get(question.getId());

            boolean isCorrect = false;

            if (submittedAnswer != null) {
                if (question.getType() == QuestionType.SINGLE_CHOICE) {
                    isCorrect = checkSingleChoiceAnswer(question, submittedAnswer.selectedAnswerIds());
                } else if (question.getType() == QuestionType.MULTIPLE_CHOICE) {
                    isCorrect = checkMultipleChoiceAnswer(question, submittedAnswer.selectedAnswerIds());
                }
            }

            if (isCorrect) {
                correctAnswers++;
                earnedScore += question.getScore();
            }

            // Get correct answer IDs
            Set<UUID> correctAnswerIds = question.getAnswers().stream()
                    .filter(Answer::getIsCorrect)
                    .map(Answer::getId)
                    .collect(Collectors.toSet());

            questionResults.add(new ExamResultResponseDTO.QuestionResultDTO(
                    question.getId(),
                    question.getContent(),
                    question.getScore().doubleValue(),
                    submittedAnswer != null ? submittedAnswer.selectedAnswerIds() : Collections.emptyList(),
                    correctAnswerIds,
                    isCorrect
            ));
        }

        // Calculate percentage
        double scorePercentage = totalScore > 0 ? (earnedScore / totalScore) * 100 : 0.0;
        boolean passed = scorePercentage >= PASS_THRESHOLD;

        // Save submission
        QuizSubmission submission = QuizSubmission.builder()
                .user(user)
                .quiz(quiz)
                .score(earnedScore)
                .totalQuestions(totalQuestions)
                .correctAnswers(correctAnswers)
                .passed(passed)
                .submissionTime(LocalDateTime.now())
                .build();

        QuizSubmission savedSubmission = submissionRepository.save(submission);

        return new ExamResultResponseDTO(
                savedSubmission.getId(),
                quiz.getTitle(),
                earnedScore,
                totalScore,
                scorePercentage,
                totalQuestions,
                correctAnswers,
                passed,
                questionResults,
                savedSubmission.getSubmissionTime()
        );
    }

    private boolean checkSingleChoiceAnswer(Question question, List<UUID> selectedAnswerIds) {
        if (selectedAnswerIds == null || selectedAnswerIds.size() != 1) {
            return false;
        }

        UUID selectedAnswerId = selectedAnswerIds.get(0);
        
        return question.getAnswers().stream()
                .filter(answer -> answer.getId().equals(selectedAnswerId))
                .findFirst()
                .map(Answer::getIsCorrect)
                .orElse(false);
    }

    private boolean checkMultipleChoiceAnswer(Question question, List<UUID> selectedAnswerIds) {
        if (selectedAnswerIds == null || selectedAnswerIds.isEmpty()) {
            return false;
        }

        Set<UUID> correctAnswerIds = question.getAnswers().stream()
                .filter(Answer::getIsCorrect)
                .map(Answer::getId)
                .collect(Collectors.toSet());

        return new HashSet<>(selectedAnswerIds).equals(correctAnswerIds);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizSubmissionResponseDTO getSubmissionById(UUID submissionId) {
        QuizSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id: " + submissionId));
        return submissionMapper.toResponseDTO(submission);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuizSubmissionResponseDTO> getUserSubmissions(UUID userId, Pageable pageable) {
        return submissionRepository.findByUserId(userId, pageable)
                .map(submissionMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuizSubmissionResponseDTO> getQuizSubmissions(UUID quizId, Pageable pageable) {
        return submissionRepository.findByQuizId(quizId, pageable)
                .map(submissionMapper::toResponseDTO);
    }
}
