package com.example.quizz.config;

import com.example.quizz.entity.Role;
import com.example.quizz.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    @Bean
    public CommandLineRunner initializeData(RoleRepository roleRepository) {
        return args -> {
            // Create default roles if they don't exist
            if (!roleRepository.existsByName("USER")) {
                Role userRole = Role.builder()
                        .name("USER")
                        .build();
                roleRepository.save(userRole);
            }

            if (!roleRepository.existsByName("ADMIN")) {
                Role adminRole = Role.builder()
                        .name("ADMIN")
                        .build();
                roleRepository.save(adminRole);
            }
        };
    }
}
