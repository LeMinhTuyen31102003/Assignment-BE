package com.example.quizz.config;

import com.example.quizz.security.CustomAccessDeniedHandler;
import com.example.quizz.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security Configuration with JWT Authentication
 * Implements stateless authentication using JWT tokens
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // Enable @PreAuthorize, @Secured annotations
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String ADMIN_ROLE = "ADMIN";
    private static final String USERS_ENDPOINT = "/api/v1/users/**";

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    /**
     * BCrypt Password Encoder Bean
     * Uses BCrypt hashing algorithm with default strength of 10
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Security Filter Chain Configuration
     * Defines which endpoints are public and which require authentication
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        try {
            http
                .csrf(csrf -> csrf.disable())  // Disable CSRF for stateless JWT
                .authorizeHttpRequests(auth -> auth
                    // Public endpoints (no authentication required)
                    .requestMatchers(
                        "/api/v1/auth/**",              // Authentication endpoints
                        "/swagger-ui/**",                // Swagger UI
                        "/v3/api-docs/**",              // OpenAPI docs
                        "/swagger-ui.html",
                        "/actuator/health",             // Health check
                        "/actuator/info"                // Info endpoint
                    ).permitAll()
                    
                    // Admin-only endpoints
                    .requestMatchers(HttpMethod.POST, "/api/v1/users").hasRole(ADMIN_ROLE)
                    .requestMatchers(HttpMethod.PUT, USERS_ENDPOINT).hasRole(ADMIN_ROLE)
                    .requestMatchers(HttpMethod.DELETE, USERS_ENDPOINT).hasRole(ADMIN_ROLE)
                    .requestMatchers(HttpMethod.GET, USERS_ENDPOINT).hasRole(ADMIN_ROLE)
                    
                    // All other endpoints require authentication
                    .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // No session, use JWT
                )
                .exceptionHandling(exception -> exception
                    .accessDeniedHandler(accessDeniedHandler)  // Custom 403 handler
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to configure security", e);
        }
    }

    /**
     * Authentication Provider
     * Configures how Spring Security authenticates users
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Authentication Manager Bean
     * Required for manual authentication in login endpoint
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
        try {
            return config.getAuthenticationManager();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get authentication manager", e);
        }
    }
}

