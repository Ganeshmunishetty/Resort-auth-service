package com.example.auth.controller;

import com.example.auth.dto.AuthResponse;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ----------------------
    // Registration Endpoints
    // ----------------------

    // USER registration
    @PostMapping("/register/user")
    public String registerUser(@Valid @RequestBody RegisterRequest request) {
        authService.register(request, "USER", true);
        return "User registered successfully";
    }

    // OWNER registration
    @PostMapping("/register/owner")
    public String registerOwner(@Valid @RequestBody RegisterRequest request) {
        authService.register(request, "OWNER", false);
        return "Owner registered. Waiting for admin approval";
    }

    // ADMIN registration (optional/internal)
    @PostMapping("/register/admin")
    public String registerAdmin(@Valid @RequestBody RegisterRequest request) {
        authService.register(request, "ADMIN", true);
        return "Admin registered successfully";
    }

    // ----------------------
    // Login Endpoints
    // ----------------------

    @PostMapping("/login/user")
    public AuthResponse loginUser(@RequestBody LoginRequest request) {
        return authService.login(request, "USER");
    }

    @PostMapping("/login/owner")
    public AuthResponse loginOwner(@RequestBody LoginRequest request) {
        return authService.login(request, "OWNER");
    }

    @PostMapping("/login/admin")
    public AuthResponse loginAdmin(@RequestBody LoginRequest request) {
        return authService.login(request, "ADMIN");
    }

    // ----------------------
    // Protected Endpoints
    // ----------------------

    @GetMapping("/user/profile")
    public String getUserProfile(Authentication authentication) {
        String email = authentication.getName();
        return "USER Profile: email=" + email;
    }

    @GetMapping("/owner/profile")
    public String getOwnerProfile(Authentication authentication) {
        String email = authentication.getName();
        return "OWNER Profile: email=" + email;
    }

    @GetMapping("/admin/profile")
    public String getAdminProfile(Authentication authentication) {
        String email = authentication.getName();
        return "ADMIN Profile: email=" + email;
    }
}

