package com.example.auth.service.impl;

import com.example.auth.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@authservice.com}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${spring.mail.enabled:false}")
    private boolean emailEnabled;

    @Override
    public void sendPasswordResetEmail(String to, String resetToken) {
        if (!emailEnabled) {
            log.info("Email disabled. Password reset token for {}: {}", to, resetToken);
            log.info("Reset link: {}/reset-password?token={}", frontendUrl, resetToken);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Password Reset Request");
            message.setText(String.format(
                "Hello,\n\n" +
                "You requested to reset your password. Click the link below to reset it:\n\n" +
                "%s/reset-password?token=%s\n\n" +
                "This link will expire in 15 minutes.\n\n" +
                "If you didn't request this, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Auth Service Team",
                frontendUrl, resetToken
            ));

            mailSender.send(message);
            log.info("Password reset email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", to, e);
        }
    }

    @Override
    public void sendWelcomeEmail(String to, String username) {
        if (!emailEnabled) {
            log.info("Email disabled. Welcome email would be sent to: {}", to);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Welcome to Auth Service!");
            message.setText(String.format(
                "Hello %s,\n\n" +
                "Welcome to Auth Service! Your account has been successfully created.\n\n" +
                "You can now log in at: %s\n\n" +
                "Best regards,\n" +
                "Auth Service Team",
                username, frontendUrl
            ));

            mailSender.send(message);
            log.info("Welcome email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", to, e);
        }
    }

    @Override
    public void sendAccountApprovalEmail(String to, String username) {
        if (!emailEnabled) {
            log.info("Email disabled. Approval email would be sent to: {}", to);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Account Approved!");
            message.setText(String.format(
                "Hello %s,\n\n" +
                "Great news! Your account has been approved by an administrator.\n\n" +
                "You can now log in at: %s\n\n" +
                "Best regards,\n" +
                "Auth Service Team",
                username, frontendUrl
            ));

            mailSender.send(message);
            log.info("Approval email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send approval email to: {}", to, e);
        }
    }
}
