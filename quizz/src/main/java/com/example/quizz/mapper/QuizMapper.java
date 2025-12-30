package com.example.quizz.mapper;

import com.example.quizz.dto.request.QuizRequestDTO;
import com.example.quizz.dto.response.QuizResponseDTO;
import com.example.quizz.entity.Question;
import com.example.quizz.entity.Quiz;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QuizMapper {

    Quiz toEntity(QuizRequestDTO dto);

    QuizResponseDTO toResponseDTO(Quiz quiz);

    QuizResponseDTO toResponseDTOWithoutQuestions(Quiz quiz);

    default List<QuizResponseDTO.QuestionSummaryDTO> mapQuestions(List<Question> questions) {
        if (questions == null) {
            return List.of();
        }
        return questions.stream()
                .map(q -> new QuizResponseDTO.QuestionSummaryDTO(
                        q.getId(),
                        q.getContent(),
                        q.getScore()
                ))
                .toList();
    }
}
