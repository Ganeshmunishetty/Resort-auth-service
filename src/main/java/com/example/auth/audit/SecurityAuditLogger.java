package com.example.auth.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class SecurityAuditLogger {

    public void logLoginSuccess(String email, String role, String ipAddress) {
        log.info("LOGIN_SUCCESS | email={} | role={} | ip={} | timestamp={}", 
            email, role, ipAddress, LocalDateTime.now());
    }

    public void logLoginFailure(String email, String reason, String ipAddress) {
        log.warn("LOGIN_FAILURE | email={} | reason={} | ip={} | timestamp={}", 
            email, reason, ipAddress, LocalDateTime.now());
    }

    public void logAccountLocked(String email, String ipAddress) {
        log.warn("ACCOUNT_LOCKED | email={} | ip={} | timestamp={}", 
            email, ipAddress, LocalDateTime.now());
    }

    public void logRegistration(String email, String role, String status) {
        log.info("REGISTRATION | email={} | role={} | status={} | timestamp={}", 
            email, role, status, LocalDateTime.now());
    }

    public void logPasswordReset(String email, String ipAddress) {
        log.info("PASSWORD_RESET | email={} | ip={} | timestamp={}", 
            email, ipAddress, LocalDateTime.now());
    }

    public void logPasswordResetRequest(String email, String ipAddress) {
        log.info("PASSWORD_RESET_REQUEST | email={} | ip={} | timestamp={}", 
            email, ipAddress, LocalDateTime.now());
    }

    public void logUnauthorizedAccess(String email, String resource, String ipAddress) {
        log.warn("UNAUTHORIZED_ACCESS | email={} | resource={} | ip={} | timestamp={}", 
            email, resource, ipAddress, LocalDateTime.now());
    }

    public void logTokenValidationFailure(String token, String reason, String ipAddress) {
        log.warn("TOKEN_VALIDATION_FAILURE | reason={} | ip={} | timestamp={}", 
            reason, ipAddress, LocalDateTime.now());
    }
}
