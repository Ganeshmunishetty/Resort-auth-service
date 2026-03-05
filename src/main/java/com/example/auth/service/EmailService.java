package com.example.auth.service;

public interface EmailService {
    
    void sendPasswordResetEmail(String to, String resetToken);
    
    void sendWelcomeEmail(String to, String username);
    
    void sendAccountApprovalEmail(String to, String username);
}
