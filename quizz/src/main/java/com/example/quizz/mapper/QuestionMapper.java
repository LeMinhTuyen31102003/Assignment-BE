package com.example.quizz.mapper;

import com.example.quizz.dto.request.AnswerRequestDTO;
import com.example.quizz.dto.request.QuestionRequestDTO;
import com.example.quizz.dto.response.AnswerResponseDTO;
import com.example.quizz.dto.response.QuestionResponseDTO;
import com.example.quizz.entity.Answer;
import com.example.quizz.entity.Question;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

    @Mapping(target = "answers", source = "answers")
    Question toEntity(QuestionRequestDTO dto);

    @Mapping(target = "question", ignore = true)
    Answer toAnswerEntity(AnswerRequestDTO dto);

    QuestionResponseDTO toResponseDTO(Question question);

    List<Answer> toAnswerEntityList(List<AnswerRequestDTO> dtos);
}
