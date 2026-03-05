package com.example.auth.controller;

import com.example.auth.dto.*;
import com.example.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {

    private final AuthService authService;

    // ----------------------
    // Registration Endpoints
    // ----------------------

    @Operation(summary = "Register new user", description = "Register a new user account with automatic approval")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or email already exists"),
        @ApiResponse(responseCode = "429", description = "Too many requests")
    })
    @PostMapping("/register/user")
    public String registerUser(@Valid @RequestBody RegisterRequest request) {
        authService.register(request, "USER", true);
        return "User registered successfully";
    }

    @Operation(summary = "Register new owner", description = "Register a new owner account (requires admin approval)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Owner registered, pending approval"),
        @ApiResponse(responseCode = "400", description = "Invalid input or email already exists"),
        @ApiResponse(responseCode = "429", description = "Too many requests")
    })
    @PostMapping("/register/owner")
    public String registerOwner(@Valid @RequestBody RegisterRequest request) {
        authService.register(request, "OWNER", false);
        return "Owner registered. Waiting for admin approval";
    }

    @Operation(summary = "Register new admin", description = "Register a new admin account (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Admin registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or email already exists"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Admin access required"),
        @ApiResponse(responseCode = "429", description = "Too many requests")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/register/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String registerAdmin(@Valid @RequestBody RegisterRequest request) {
        authService.register(request, "ADMIN", true);
        return "Admin registered successfully";
    }

    // ----------------------
    // Login Endpoints
    // ----------------------

    @Operation(summary = "User login", description = "Authenticate user and receive JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful", 
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid credentials or account locked"),
        @ApiResponse(responseCode = "429", description = "Too many requests")
    })
    @PostMapping("/login/user")
    public AuthResponse loginUser(@Valid @RequestBody LoginRequest request, 
                                   HttpServletRequest httpRequest) {
        return authService.login(request, "USER", getClientIP(httpRequest));
    }

    @Operation(summary = "Owner login", description = "Authenticate owner and receive JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid credentials, account not approved, or locked"),
        @ApiResponse(responseCode = "429", description = "Too many requests")
    })
    @PostMapping("/login/owner")
    public AuthResponse loginOwner(@Valid @RequestBody LoginRequest request,
                                    HttpServletRequest httpRequest) {
        return authService.login(request, "OWNER", getClientIP(httpRequest));
    }

    @Operation(summary = "Admin login", description = "Authenticate admin and receive JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid credentials or account locked"),
        @ApiResponse(responseCode = "429", description = "Too many requests")
    })
    @PostMapping("/login/admin")
    public AuthResponse loginAdmin(@Valid @RequestBody LoginRequest request,
                                    HttpServletRequest httpRequest) {
        return authService.login(request, "ADMIN", getClientIP(httpRequest));
    }

    // ----------------------
    // Password Reset Endpoints
    // ----------------------

    @Operation(summary = "Request password reset", description = "Request a password reset token via email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset email sent"),
        @ApiResponse(responseCode = "404", description = "Email not found")
    })
    @PostMapping("/forgot-password")
    public String forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return authService.requestPasswordReset(request);
    }

    @Operation(summary = "Reset password", description = "Reset password using token from email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset successful"),
        @ApiResponse(responseCode = "400", description = "Invalid or expired token")
    })
    @PostMapping("/reset-password")
    public String resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        return authService.resetPassword(request);
    }

    // ----------------------
    // Protected Endpoints
    // ----------------------

    @Operation(summary = "Get user profile", description = "Get authenticated user profile", 
        security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/user/profile")
    public String getUserProfile(@Parameter(hidden = true) Authentication authentication) {
        String email = authentication.getName();
        return "USER Profile: email=" + email;
    }

    @Operation(summary = "Get owner profile", description = "Get authenticated owner profile",
        security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/owner/profile")
    public String getOwnerProfile(@Parameter(hidden = true) Authentication authentication) {
        String email = authentication.getName();
        return "OWNER Profile: email=" + email;
    }

    @Operation(summary = "Get admin profile", description = "Get authenticated admin profile",
        security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/admin/profile")
    public String getAdminProfile(@Parameter(hidden = true) Authentication authentication) {
        String email = authentication.getName();
        return "ADMIN Profile: email=" + email;
    }

    // ----------------------
    // Helper Methods
    // ----------------------

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
