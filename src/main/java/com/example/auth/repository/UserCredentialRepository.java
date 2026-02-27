package com.example.auth.repository;

import com.example.auth.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {

    // ✅ used in register()
    boolean existsByEmail(String email);

    // ✅ FIX for your error
    boolean existsByUsername(String username);

    // ✅ used in login()
    Optional<UserCredential> findByEmail(String email);
}