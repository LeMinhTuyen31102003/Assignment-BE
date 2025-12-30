package com.example.quizz.service;

import com.example.quizz.dto.request.LoginRequestDTO;
import com.example.quizz.dto.request.RefreshTokenRequestDTO;
import com.example.quizz.dto.request.UserRequestDTO;
import com.example.quizz.dto.response.AuthResponseDTO;
import com.example.quizz.dto.response.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    UserResponseDTO createUser(UserRequestDTO requestDTO);
    UserResponseDTO getUserById(UUID id);
    Page<UserResponseDTO> getAllUsers(Pageable pageable);
    UserResponseDTO updateUser(UUID id, UserRequestDTO requestDTO);
    void deleteUser(UUID id);
    AuthResponseDTO register(UserRequestDTO requestDTO);
    AuthResponseDTO login(LoginRequestDTO requestDTO);
    AuthResponseDTO refreshToken(RefreshTokenRequestDTO requestDTO);
}
