package com.example.quizz.service.impl;

import com.example.quizz.dto.request.QuizRequestDTO;
import com.example.quizz.dto.response.QuizResponseDTO;
import com.example.quizz.entity.Question;
import com.example.quizz.entity.Quiz;
import com.example.quizz.exception.ResourceNotFoundException;
import com.example.quizz.mapper.QuizMapper;
import com.example.quizz.repository.QuestionRepository;
import com.example.quizz.repository.QuizRepository;
import com.example.quizz.service.QuizService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuizMapper quizMapper;

    @Override
    @Transactional
    public QuizResponseDTO createQuiz(QuizRequestDTO requestDTO) {
        Quiz quiz = quizMapper.toEntity(requestDTO);
        
        // Add questions if provided
        if (requestDTO.questionIds() != null && !requestDTO.questionIds().isEmpty()) {
            for (UUID questionId : requestDTO.questionIds()) {
                Question question = questionRepository.findById(questionId)
                        .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));
                quiz.addQuestion(question);
            }
        }
        
        Quiz savedQuiz = quizRepository.save(quiz);
        return quizMapper.toResponseDTO(savedQuiz);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResponseDTO getQuizById(UUID id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + id));
        return quizMapper.toResponseDTO(quiz);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuizResponseDTO> getAllQuizzes(Pageable pageable) {
        return quizRepository.findAllByIsActive(true, pageable)
                .map(quizMapper::toResponseDTO);
    }

    @Override
    @Transactional
    public QuizResponseDTO updateQuiz(UUID id, QuizRequestDTO requestDTO) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + id));

        quiz.setTitle(requestDTO.title());
        quiz.setDescription(requestDTO.description());
        quiz.setDurationMinutes(requestDTO.durationMinutes());

        // Update questions if provided
        if (requestDTO.questionIds() != null) {
            quiz.clearQuestions();
            for (UUID questionId : requestDTO.questionIds()) {
                Question question = questionRepository.findById(questionId)
                        .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));
                quiz.addQuestion(question);
            }
        }

        Quiz updatedQuiz = quizRepository.save(quiz);
        return quizMapper.toResponseDTO(updatedQuiz);
    }

    @Override
    @Transactional
    public void deleteQuiz(UUID id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + id));
        quiz.setIsActive(false);
        quizRepository.save(quiz);
    }

    @Override
    @Transactional
    public QuizResponseDTO addQuestionToQuiz(UUID quizId, UUID questionId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));
        
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        quiz.addQuestion(question);
        Quiz updatedQuiz = quizRepository.save(quiz);
        return quizMapper.toResponseDTO(updatedQuiz);
    }

    @Override
    @Transactional
    public QuizResponseDTO removeQuestionFromQuiz(UUID quizId, UUID questionId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));
        
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        quiz.removeQuestion(question);
        Quiz updatedQuiz = quizRepository.save(quiz);
        return quizMapper.toResponseDTO(updatedQuiz);
    }
}
