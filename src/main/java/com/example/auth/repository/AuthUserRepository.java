package com.example.auth.repository;

import com.example.auth.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthUserRepository extends JpaRepository<UserCredential, Long> {

    Optional<UserCredential> findByUsername(String username);

    Optional<UserCredential> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}