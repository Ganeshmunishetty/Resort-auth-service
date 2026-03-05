package com.example.auth.service;

import com.example.auth.audit.SecurityAuditLogger;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.dto.AuthResponse;
import com.example.auth.entity.UserCredential;
import com.example.auth.exception.BadRequestException;
import com.example.auth.repository.UserCredentialRepository;
import com.example.auth.service.impl.AuthServiceImpl;
import com.example.auth.util.JwtUtil;
import com.example.auth.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserCredentialRepository userRepository;

    @Mock
    private PasswordUtil passwordUtil;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private SecurityAuditLogger auditLogger;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private UserCredential user;

    @BeforeEach
    void setUp() throws Exception {
        // Set maxFailedAttempts to 5 using reflection to match production behavior
        var field = AuthServiceImpl.class.getDeclaredField("maxFailedAttempts");
        field.setAccessible(true);
        field.set(authService, 5);
        
        // Set lockoutDurationMinutes to 30
        var lockoutField = AuthServiceImpl.class.getDeclaredField("lockoutDurationMinutes");
        lockoutField.setAccessible(true);
        lockoutField.set(authService, 30);
        
        // Set resetTokenExpirationMs
        var resetField = AuthServiceImpl.class.getDeclaredField("resetTokenExpirationMs");
        resetField.setAccessible(true);
        resetField.set(authService, 900000L);
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("Password123");
        registerRequest.setConfirmPassword("Password123");
        registerRequest.setAge(25);
        registerRequest.setPhoneNumber("1234567890");
        registerRequest.setGender("Male");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("Password123");

        user = UserCredential.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .role("USER")
                .status("APPROVED")
                .failedLoginAttempts(0)
                .build();
    }

    @Test
    void testRegister_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordUtil.encode(anyString())).thenReturn("encodedPassword");
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("jwt-token");
        when(userRepository.save(any(UserCredential.class))).thenReturn(user);

        AuthResponse response = authService.register(registerRequest, "USER", true);

        assertNotNull(response);
        assertEquals("USER", response.getRole());
        assertEquals("APPROVED", response.getStatus());
        assertNotNull(response.getToken());
        verify(userRepository).save(any(UserCredential.class));
        verify(auditLogger).logRegistration(anyString(), anyString(), anyString());
    }

    @Test
    void testRegister_DuplicateEmail() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            authService.register(registerRequest, "USER", true);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    void testRegister_PasswordMismatch() {
        registerRequest.setConfirmPassword("DifferentPassword");
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        assertThrows(BadRequestException.class, () -> {
            authService.register(registerRequest, "USER", true);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    void testLogin_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordUtil.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("jwt-token");

        AuthResponse response = authService.login(loginRequest, "USER", "127.0.0.1");

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("USER", response.getRole());
        verify(auditLogger).logLoginSuccess(anyString(), anyString(), anyString());
    }

    @Test
    void testLogin_InvalidPassword() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordUtil.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(BadRequestException.class, () -> {
            authService.login(loginRequest, "USER", "127.0.0.1");
        });

        verify(auditLogger).logLoginFailure(anyString(), anyString(), anyString());
        verify(userRepository).save(any(UserCredential.class));
    }

    @Test
    void testLogin_AccountLocked() {
        user.lockAccount(30);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> {
            authService.login(loginRequest, "USER", "127.0.0.1");
        });

        verify(auditLogger).logLoginFailure(anyString(), anyString(), anyString());
    }

    @Test
    void testLogin_RoleMismatch() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordUtil.matches(anyString(), anyString())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            authService.login(loginRequest, "ADMIN", "127.0.0.1");
        });

        verify(auditLogger).logLoginFailure(anyString(), anyString(), anyString());
    }

    @Test
    void testLogin_PendingApproval() {
        user.setStatus("PENDING");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordUtil.matches(anyString(), anyString())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            authService.login(loginRequest, "USER", "127.0.0.1");
        });

        verify(auditLogger).logLoginFailure(anyString(), anyString(), anyString());
    }
}
