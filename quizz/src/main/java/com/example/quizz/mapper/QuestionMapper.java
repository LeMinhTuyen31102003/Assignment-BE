package com.example.quizz.mapper;

import com.example.quizz.dto.request.AnswerRequestDTO;
import com.example.quizz.dto.request.QuestionRequestDTO;
import com.example.quizz.dto.response.AnswerResponseDTO;
import com.example.quizz.dto.response.QuestionResponseDTO;
import com.example.quizz.entity.Answer;
import com.example.quizz.entity.Question;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuestionMapper {

    public Question toEntity(QuestionRequestDTO dto) {
        Question question = Question.builder()
                .content(dto.content())
                .type(dto.type())
                .score(dto.score())
                .build();

        if (dto.answers() != null) {
            List<Answer> answers = dto.answers().stream()
                    .map(answerDTO -> toAnswerEntity(answerDTO, question))
                    .collect(Collectors.toList());
            question.getAnswers().addAll(answers);
        }

        return question;
    }

    public Answer toAnswerEntity(AnswerRequestDTO dto, Question question) {
        return Answer.builder()
                .content(dto.content())
                .isCorrect(dto.isCorrect())
                .question(question)
                .build();
    }

    public QuestionResponseDTO toResponseDTO(Question question) {
        List<AnswerResponseDTO> answerDTOs = question.getAnswers().stream()
                .map(this::toAnswerResponseDTO)
                .collect(Collectors.toList());

        return new QuestionResponseDTO(
                question.getId(),
                question.getContent(),
                question.getType(),
                question.getScore(),
                answerDTOs,
                question.getCreatedAt(),
                question.getUpdatedAt()
        );
    }

    public AnswerResponseDTO toAnswerResponseDTO(Answer answer) {
        return new AnswerResponseDTO(
                answer.getId(),
                answer.getContent(),
                answer.getIsCorrect()
        );
    }
}
