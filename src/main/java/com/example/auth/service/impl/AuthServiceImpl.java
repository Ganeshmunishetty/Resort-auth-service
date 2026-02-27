package com.example.auth.service.impl;

import com.example.auth.dto.*;
import com.example.auth.entity.UserCredential;
import com.example.auth.exception.BadRequestException;
import com.example.auth.exception.ResourceNotFoundException;
import com.example.auth.repository.UserCredentialRepository;
import com.example.auth.service.AuthService;
import com.example.auth.util.JwtUtil;
import com.example.auth.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserCredentialRepository userRepository;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;

    // ===============================
    // REGISTER
    // ===============================
    @Override
    public AuthResponse register(RegisterRequest request,
                                 String role,
                                 boolean autoApprove) {

        // 🔴 check email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        // 🔴 check username exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already taken");
        }

        // 🔴 password match check
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        // ✅ status logic
        String status = autoApprove ? "APPROVED" : "PENDING";

        // ✅ build entity
        UserCredential user = UserCredential.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .age(request.getAge())
                .gender(request.getGender())
                .role(role) // USER / OWNER / ADMIN
                .status(status)
                .password(passwordUtil.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        // ✅ generate token only if approved
        String token = null;
        if ("APPROVED".equals(status)) {
            token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        }

        return AuthResponse.builder()
                .token(token)
                .role(user.getRole())
                .status(user.getStatus())
                .message("Registration successful")
                .build();
    }

    // ===============================
    // LOGIN (ROLE-BASED URL SAFE)
    // ===============================
    @Override
    public AuthResponse login(LoginRequest request, String expectedRole) {

        UserCredential user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid credentials"));

        // 🔴 password check
        if (!passwordUtil.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        // 🔴 role mismatch protection (VERY IMPORTANT)
        if (!user.getRole().equalsIgnoreCase(expectedRole)) {
            throw new BadRequestException("You are not allowed to login from this portal");
        }

        // 🔴 approval check
        if (!"APPROVED".equalsIgnoreCase(user.getStatus())) {
            throw new BadRequestException("Account is pending admin approval");
        }

        // ✅ generate JWT
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return AuthResponse.builder()
                .token(token)
                .role(user.getRole())
                .username(user.getUsername()) 
                .status(user.getStatus())
                .message("Login successful")
                .build();
    }

    // ===============================
    // FORGOT PASSWORD
    // ===============================
    @Override
    public String forgotPassword(ForgotPasswordRequest request) {

        UserCredential user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Email not registered"));

        user.setPassword(passwordUtil.encode(request.getNewPassword()));
        userRepository.save(user);

        return "Password reset successful";
    }
}