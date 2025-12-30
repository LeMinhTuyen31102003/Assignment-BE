package com.example.quizz.repository;

import com.example.quizz.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findById(UUID id);

    @EntityGraph(attributePaths = {"roles"})
    Page<User> findAllByIsActive(Boolean isActive, Pageable pageable);

    boolean existsByEmail(String email);
}
