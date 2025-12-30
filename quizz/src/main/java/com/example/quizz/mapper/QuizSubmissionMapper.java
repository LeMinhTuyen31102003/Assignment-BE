package com.example.quizz.mapper;

import com.example.quizz.dto.response.QuizSubmissionResponseDTO;
import com.example.quizz.entity.QuizSubmission;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface QuizSubmissionMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "quizId", source = "quiz.id")
    @Mapping(target = "quizTitle", source = "quiz.title")
    QuizSubmissionResponseDTO toResponseDTO(QuizSubmission submission);
}
