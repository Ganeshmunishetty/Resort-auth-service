package com.example.auth.service.impl;

import com.example.auth.audit.SecurityAuditLogger;
import com.example.auth.dto.*;
import com.example.auth.entity.UserCredential;
import com.example.auth.exception.BadRequestException;
import com.example.auth.exception.ResourceNotFoundException;
import com.example.auth.repository.UserCredentialRepository;
import com.example.auth.service.AuthService;
import com.example.auth.service.EmailService;
import com.example.auth.util.JwtUtil;
import com.example.auth.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserCredentialRepository userRepository;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;
    private final SecurityAuditLogger auditLogger;
    private final EmailService emailService;

    @Value("${account.lockout.max.attempts:5}")
    private int maxFailedAttempts;

    @Value("${account.lockout.duration.minutes:30}")
    private int lockoutDurationMinutes;

    @Value("${password.reset.token.expirationMs:900000}")
    private long resetTokenExpirationMs;

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
                .countryCode(request.getCountryCode())
                .age(request.getAge())
                .gender(request.getGender())
                .role(role)
                .status(status)
                .password(passwordUtil.encode(request.getPassword()))
                .failedLoginAttempts(0)
                .build();

        userRepository.save(user);

        // ✅ audit log
        auditLogger.logRegistration(user.getEmail(), user.getRole(), user.getStatus());

        // ✅ send welcome email
        if ("APPROVED".equals(status)) {
            emailService.sendWelcomeEmail(user.getEmail(), user.getUsername());
        }

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
    // LOGIN (WITH ACCOUNT LOCKOUT)
    // ===============================
    @Override
    public AuthResponse login(LoginRequest request, String expectedRole, String ipAddress) {

        UserCredential user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    auditLogger.logLoginFailure(request.getEmail(), "User not found", ipAddress);
                    return new ResourceNotFoundException("Invalid credentials");
                });

        // 🔴 check if account is locked
        if (user.isLocked()) {
            auditLogger.logLoginFailure(user.getEmail(), "Account locked", ipAddress);
            throw new BadRequestException("Account is temporarily locked. Please try again later.");
        }

        // 🔴 password check
        if (!passwordUtil.matches(request.getPassword(), user.getPassword())) {
            handleFailedLogin(user, ipAddress);
            throw new BadRequestException("Invalid credentials");
        }

        // 🔴 role mismatch protection
        if (!user.getRole().equalsIgnoreCase(expectedRole)) {
            auditLogger.logLoginFailure(user.getEmail(), "Role mismatch", ipAddress);
            throw new BadRequestException("You are not allowed to login from this portal");
        }

        // 🔴 approval check
        if (!"APPROVED".equalsIgnoreCase(user.getStatus())) {
            auditLogger.logLoginFailure(user.getEmail(), "Account not approved", ipAddress);
            throw new BadRequestException("Account is pending admin approval");
        }

        // ✅ reset failed attempts on successful login
        user.resetFailedAttempts();
        userRepository.save(user);

        // ✅ audit log
        auditLogger.logLoginSuccess(user.getEmail(), user.getRole(), ipAddress);

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

    private void handleFailedLogin(UserCredential user, String ipAddress) {
        user.incrementFailedAttempts();
        
        if (user.getFailedLoginAttempts() >= maxFailedAttempts) {
            user.lockAccount(lockoutDurationMinutes);
            auditLogger.logAccountLocked(user.getEmail(), ipAddress);
        } else {
            auditLogger.logLoginFailure(user.getEmail(), 
                "Invalid password (attempt " + user.getFailedLoginAttempts() + ")", ipAddress);
        }
        
        userRepository.save(user);
    }

    // ===============================
    // PASSWORD RESET REQUEST
    // ===============================
    @Override
    public String requestPasswordReset(ForgotPasswordRequest request) {

        UserCredential user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Email not registered"));

        // Generate secure token
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(resetTokenExpirationMs / 60000));
        
        userRepository.save(user);

        // ✅ Send email with reset link
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken);

        auditLogger.logPasswordResetRequest(user.getEmail(), "system");

        return "Password reset link has been sent to your email";
    }

    // ===============================
    // PASSWORD RESET
    // ===============================
    @Override
    public String resetPassword(PasswordResetRequest request) {

        // 🔴 password match check
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        UserCredential user = userRepository.findByResetToken(request.getToken())
                .orElseThrow(() -> new BadRequestException("Invalid or expired reset token"));

        // 🔴 check token expiry
        if (user.getResetTokenExpiry() == null || 
            LocalDateTime.now().isAfter(user.getResetTokenExpiry())) {
            throw new BadRequestException("Reset token has expired");
        }

        // ✅ update password
        user.setPassword(passwordUtil.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        user.resetFailedAttempts(); // Reset lockout on password change
        
        userRepository.save(user);

        auditLogger.logPasswordReset(user.getEmail(), "system");

        return "Password reset successful";
    }
}
