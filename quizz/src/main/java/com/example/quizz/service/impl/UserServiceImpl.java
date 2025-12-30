package com.example.quizz.service.impl;

import com.example.quizz.dto.request.UserRequestDTO;
import com.example.quizz.dto.response.UserResponseDTO;
import com.example.quizz.entity.Role;
import com.example.quizz.entity.User;
import com.example.quizz.exception.DuplicateResourceException;
import com.example.quizz.exception.ResourceNotFoundException;
import com.example.quizz.mapper.UserMapper;
import com.example.quizz.repository.RoleRepository;
import com.example.quizz.repository.UserRepository;
import com.example.quizz.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        if (userRepository.existsByEmail(requestDTO.email())) {
            throw new DuplicateResourceException("Email already exists: " + requestDTO.email());
        }

        User user = userMapper.toEntity(requestDTO);
        
        // Assign default USER role
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("Role USER not found"));
        user.getRoles().add(userRole);
        
        User savedUser = userRepository.save(user);
        return userMapper.toResponseDTO(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toResponseDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAllByIsActive(true, pageable)
                .map(userMapper::toResponseDTO);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(UUID id, UserRequestDTO requestDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Check if new email already exists (for different user)
        if (!user.getEmail().equals(requestDTO.email()) && 
            userRepository.existsByEmail(requestDTO.email())) {
            throw new DuplicateResourceException("Email already exists: " + requestDTO.email());
        }

        user.setEmail(requestDTO.email());
        user.setFullName(requestDTO.fullName());
        // Note: Password update logic should be separate endpoint in production
        if (requestDTO.password() != null && !requestDTO.password().isBlank()) {
            user.setPassword(requestDTO.password());
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toResponseDTO(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setIsActive(false);
        userRepository.save(user);
    }
}
