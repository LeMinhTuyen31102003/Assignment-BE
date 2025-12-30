package com.example.quizz.security;

import com.example.quizz.entity.Role;
import com.example.quizz.entity.User;
import com.example.quizz.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Custom UserDetailsService implementation
 * Loads user from database for Spring Security authentication
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Load user by email (username in our case is email)
     * 
     * @param email User's email address
     * @return UserDetails object for Spring Security
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        log.debug("User found: {}, Active: {}", email, user.getIsActive());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getIsActive(),
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                getAuthorities(user)
        );
    }

    /**
     * Convert user roles to Spring Security GrantedAuthority
     * 
     * @param user User entity
     * @return Collection of GrantedAuthority
     */
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return user.getRoles().stream()
                .map(Role::getName)
                .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName))
                .toList();
    }
}
