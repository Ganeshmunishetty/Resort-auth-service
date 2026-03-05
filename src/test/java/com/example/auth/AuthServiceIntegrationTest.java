package com.example.auth;

import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.dto.AuthResponse;
import com.example.auth.entity.UserCredential;
import com.example.auth.exception.BadRequestException;
import com.example.auth.repository.UserCredentialRepository;
import com.example.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class AuthServiceIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserCredentialRepository userRepository;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("Password123");
        registerRequest.setConfirmPassword("Password123");
        registerRequest.setAge(25);
        registerRequest.setPhoneNumber("1234567890");
        registerRequest.setGender("Male");
    }

    @Test
    void testUserRegistration_Success() {
        AuthResponse response = authService.register(registerRequest, "USER", true);
        
        assertNotNull(response);
        assertEquals("USER", response.getRole());
        assertEquals("APPROVED", response.getStatus());
        assertNotNull(response.getToken());
    }

    @Test
    void testUserRegistration_DuplicateEmail() {
        authService.register(registerRequest, "USER", true);
        
        assertThrows(BadRequestException.class, () -> {
            authService.register(registerRequest, "USER", true);
        });
    }

    @Test
    void testLogin_Success() {
        authService.register(registerRequest, "USER", true);
        
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("Password123");
        
        AuthResponse response = authService.login(loginRequest, "USER", "127.0.0.1");
        
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("USER", response.getRole());
    }

    @Test
    void testLogin_InvalidPassword() {
        authService.register(registerRequest, "USER", true);
        
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("WrongPassword");
        
        assertThrows(BadRequestException.class, () -> {
            authService.login(loginRequest, "USER", "127.0.0.1");
        });
    }

    @Test
    void testAccountLockout_AfterFailedAttempts() {
        authService.register(registerRequest, "USER", true);
        
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("WrongPassword");
        
        // Attempt 5 failed logins
        for (int i = 0; i < 5; i++) {
            try {
                authService.login(loginRequest, "USER", "127.0.0.1");
            } catch (BadRequestException e) {
                // Expected
            }
        }
        
        // Verify account is locked
        UserCredential user = userRepository.findByEmail("test@example.com").orElseThrow();
        assertTrue(user.isLocked());
        assertEquals(5, user.getFailedLoginAttempts());
    }

    @Test
    void testOwnerRegistration_PendingApproval() {
        AuthResponse response = authService.register(registerRequest, "OWNER", false);
        
        assertNotNull(response);
        assertEquals("OWNER", response.getRole());
        assertEquals("PENDING", response.getStatus());
        assertNull(response.getToken()); // No token for pending users
    }
}
