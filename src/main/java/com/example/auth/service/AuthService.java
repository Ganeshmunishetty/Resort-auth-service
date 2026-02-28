package com.example.auth.service;

import com.example.auth.dto.*;

public interface AuthService {

    AuthResponse register(RegisterRequest request, String role, boolean autoApprove);

    AuthResponse login(LoginRequest request, String expectedRole);

    String forgotPassword(ForgotPasswordRequest request);
    
    
}