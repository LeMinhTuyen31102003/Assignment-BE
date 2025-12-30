package com.example.quizz.service.impl;

import com.example.quizz.dto.request.AnswerRequestDTO;
import com.example.quizz.dto.request.QuestionRequestDTO;
import com.example.quizz.dto.response.QuestionResponseDTO;
import com.example.quizz.entity.Answer;
import com.example.quizz.entity.Question;
import com.example.quizz.exception.ResourceNotFoundException;
import com.example.quizz.mapper.QuestionMapper;
import com.example.quizz.repository.QuestionRepository;
import com.example.quizz.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;

    @Override
    public QuestionResponseDTO createQuestion(QuestionRequestDTO requestDTO) {
        Question question = questionMapper.toEntity(requestDTO);
        question.setIsActive(true);
        
        // Set bidirectional relationship for answers
        if (question.getAnswers() != null) {
            question.getAnswers().forEach(answer -> answer.setQuestion(question));
        }
        
        Question savedQuestion = questionRepository.save(question);
        return questionMapper.toResponseDTO(savedQuestion);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponseDTO> getAllQuestions(Pageable pageable) {
        Page<Question> questions = questionRepository.findAllByIsActive(true, pageable);
        return questions.map(questionMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionResponseDTO getQuestionById(UUID id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));
        
        if (!question.getIsActive()) {
            throw new ResourceNotFoundException("Question not found with id: " + id);
        }
        
        return questionMapper.toResponseDTO(question);
    }

    @Override
    public QuestionResponseDTO updateQuestion(UUID id, QuestionRequestDTO requestDTO) {
        Question existingQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));

        if (!existingQuestion.getIsActive()) {
            throw new ResourceNotFoundException("Question not found with id: " + id);
        }

        // Update question fields
        existingQuestion.setContent(requestDTO.content());
        existingQuestion.setType(requestDTO.type());
        existingQuestion.setScore(requestDTO.score());

        // Clear existing answers
        existingQuestion.clearAnswers();

        // Add new answers
        if (requestDTO.answers() != null) {
            List<Answer> newAnswers = questionMapper.toAnswerEntityList(requestDTO.answers());
            newAnswers.forEach(answer -> {
                answer.setQuestion(existingQuestion);
                existingQuestion.addAnswer(answer);
            });
        }

        Question updatedQuestion = questionRepository.save(existingQuestion);
        return questionMapper.toResponseDTO(updatedQuestion);
    }

    @Override
    public void deleteQuestion(UUID id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));

        // Soft delete
        question.setIsActive(false);
        questionRepository.save(question);
    }
}
