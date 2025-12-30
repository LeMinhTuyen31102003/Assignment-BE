package com.example.quizz.mapper;

import com.example.quizz.dto.request.UserRequestDTO;
import com.example.quizz.dto.response.UserResponseDTO;
import com.example.quizz.entity.Role;
import com.example.quizz.entity.User;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", ignore = true)
    User toEntity(UserRequestDTO dto);

    @Mapping(target = "roles", expression = "java(mapRolesToNames(user.getRoles()))")
    UserResponseDTO toResponseDTO(User user);

    default Set<String> mapRolesToNames(Set<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}
