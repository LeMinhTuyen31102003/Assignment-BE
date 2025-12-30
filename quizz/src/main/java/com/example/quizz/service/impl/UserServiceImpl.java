package com.example.quizz.service.impl;

import com.example.quizz.dto.request.LoginRequestDTO;
import com.example.quizz.dto.request.RefreshTokenRequestDTO;
import com.example.quizz.dto.request.UserRequestDTO;
import com.example.quizz.dto.response.AuthResponseDTO;
import com.example.quizz.dto.response.UserResponseDTO;
import com.example.quizz.entity.Role;
import com.example.quizz.entity.User;
import com.example.quizz.exception.BadRequestException;
import com.example.quizz.exception.DuplicateResourceException;
import com.example.quizz.exception.ResourceNotFoundException;
import com.example.quizz.mapper.UserMapper;
import com.example.quizz.repository.RoleRepository;
import com.example.quizz.repository.UserRepository;
import com.example.quizz.security.JwtUtil;
import com.example.quizz.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        if (userRepository.existsByEmail(requestDTO.email())) {
            throw new DuplicateResourceException("Email already exists: " + requestDTO.email());
        }

        User user = userMapper.toEntity(requestDTO);
        
        // Hash password with BCrypt before saving
        user.setPassword(passwordEncoder.encode(requestDTO.password()));
        
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
        
        // Hash password with BCrypt if password is being updated
        if (requestDTO.password() != null && !requestDTO.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(requestDTO.password()));
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

    /**
     * Register a new user with BCrypt password hashing and JWT token
     * 
     * @param requestDTO User registration data
     * @return Authentication response with JWT tokens
     */
    @Override
    @Transactional
    public AuthResponseDTO register(UserRequestDTO requestDTO) {
        log.info("Registering new user with email: {}", requestDTO.email());
        
        // Check if email already exists
        if (userRepository.existsByEmail(requestDTO.email())) {
            log.warn("Registration failed: Email already exists: {}", requestDTO.email());
            throw new DuplicateResourceException("Email already exists: " + requestDTO.email());
        }

        // Create user entity
        User user = userMapper.toEntity(requestDTO);
        
        // Hash password with BCrypt
        String hashedPassword = passwordEncoder.encode(requestDTO.password());
        user.setPassword(hashedPassword);
        
        // Assign default USER role
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("Role USER not found"));
        user.getRoles().add(userRole);
        
        // Save user to database
        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getEmail());
        
        // Generate JWT tokens
        Map<String, Object> claims = buildClaims(savedUser);
        String accessToken = jwtUtil.generateToken(savedUser.getEmail(), claims);
        String refreshToken = jwtUtil.generateRefreshToken(savedUser.getEmail());
        
        log.debug("JWT tokens generated for user: {}", savedUser.getEmail());
        
        // Return response with tokens
        return new AuthResponseDTO(
            accessToken,
            refreshToken,
            userMapper.toResponseDTO(savedUser)
        );
    }

    /**
     * Login user by verifying password with BCrypt and generating JWT tokens
     * 
     * @param requestDTO Login credentials
     * @return Authentication response with JWT tokens
     */
    @Override
    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO requestDTO) {
        log.info("User login attempt: {}", requestDTO.email());
        
        // Authenticate with Spring Security
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDTO.email(),
                            requestDTO.password()
                    )
            );
            log.debug("Authentication successful for user: {}", requestDTO.email());
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", requestDTO.email());
            throw new BadRequestException("Invalid email or password");
        }

        // Find user by email
        User user = userRepository.findByEmail(requestDTO.email())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        // Check if user is active
        if (!user.getIsActive()) {
            log.warn("Login attempt for deactivated account: {}", requestDTO.email());
            throw new BadRequestException("Account is deactivated");
        }

        // Generate JWT tokens
        Map<String, Object> claims = buildClaims(user);
        String accessToken = jwtUtil.generateToken(user.getEmail(), claims);
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        
        log.info("User logged in successfully: {}", user.getEmail());

        // Return response with tokens
        return new AuthResponseDTO(
            accessToken,
            refreshToken,
            userMapper.toResponseDTO(user)
        );
    }

    /**
     * Refresh access token using refresh token
     * 
     * @param requestDTO Refresh token request
     * @return New authentication response with new access token
     */
    @Override
    @Transactional(readOnly = true)
    public AuthResponseDTO refreshToken(RefreshTokenRequestDTO requestDTO) {
        String refreshToken = requestDTO.refreshToken();
        log.debug("Refresh token request received");
        
        try {
            // Validate and extract username from refresh token
            String username = jwtUtil.extractUsername(refreshToken);
            
            if (username != null && jwtUtil.validateToken(refreshToken)) {
                // Load user from database
                User user = userRepository.findByEmail(username)
                        .orElseThrow(() -> new BadRequestException("User not found"));

                // Check if user is active
                if (!user.getIsActive()) {
                    throw new BadRequestException("Account is deactivated");
                }

                // Generate new access token
                Map<String, Object> claims = buildClaims(user);
                String newAccessToken = jwtUtil.generateToken(user.getEmail(), claims);
                
                log.info("Access token refreshed for user: {}", username);

                // Return response with new access token and same refresh token
                return new AuthResponseDTO(
                    newAccessToken,
                    refreshToken,
                    userMapper.toResponseDTO(user)
                );
            } else {
                throw new BadRequestException("Invalid refresh token");
            }
        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            throw new BadRequestException("Invalid or expired refresh token");
        }
    }

    /**
     * Build JWT claims with user information
     */
    private Map<String, Object> buildClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId().toString());
        claims.put("email", user.getEmail());
        claims.put("fullName", user.getFullName());
        claims.put("roles", user.getRoles().stream()
                .map(Role::getName)
                .toList());
        return claims;
    }
}
