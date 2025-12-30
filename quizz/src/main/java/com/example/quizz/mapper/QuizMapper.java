package com.example.quizz.mapper;

import com.example.quizz.dto.request.QuizRequestDTO;
import com.example.quizz.dto.response.QuizResponseDTO;
import com.example.quizz.entity.Question;
import com.example.quizz.entity.Quiz;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface QuizMapper {

    @Mapping(target = "questions", ignore = true)
    Quiz toEntity(QuizRequestDTO dto);

    @Mapping(target = "totalQuestions", expression = "java(quiz.getQuestions().size())")
    @Mapping(target = "questions", expression = "java(mapQuestions(quiz.getQuestions()))")
    QuizResponseDTO toResponseDTO(Quiz quiz);

    @Mapping(target = "totalQuestions", expression = "java(quiz.getQuestions().size())")
    @Mapping(target = "questions", ignore = true)
    QuizResponseDTO toResponseDTOWithoutQuestions(Quiz quiz);

    default List<QuizResponseDTO.QuestionSummaryDTO> mapQuestions(List<Question> questions) {
        return questions.stream()
                .map(q -> new QuizResponseDTO.QuestionSummaryDTO(
                        q.getId(),
                        q.getContent(),
                        q.getScore()
                ))
                .collect(Collectors.toList());
    }
}
