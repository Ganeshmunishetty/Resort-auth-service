package com.example.auth.repository;

import com.example.auth.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<UserCredential> findByEmail(String email);
    
    Optional<UserCredential> findByResetToken(String resetToken);
    
    List<UserCredential> findByStatus(String status);
}