package com.example.auth.controller;

import com.example.auth.entity.UserCredential;
import com.example.auth.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "Get pending approvals", description = "Get list of users pending approval")
    @GetMapping("/pending-approvals")
    public List<UserCredential> getPendingApprovals() {
        return adminService.getPendingApprovals();
    }

    @Operation(summary = "Approve user", description = "Approve a pending user account")
    @PostMapping("/approve/{userId}")
    public String approveUser(@PathVariable Long userId) {
        return adminService.approveUser(userId);
    }

    @Operation(summary = "Reject user", description = "Reject a pending user account")
    @PostMapping("/reject/{userId}")
    public String rejectUser(@PathVariable Long userId) {
        return adminService.rejectUser(userId);
    }

    @Operation(summary = "Get all users", description = "Get list of all users")
    @GetMapping("/users")
    public List<UserCredential> getAllUsers() {
        return adminService.getAllUsers();
    }

    @Operation(summary = "Unlock user account", description = "Manually unlock a locked user account")
    @PostMapping("/unlock/{userId}")
    public String unlockUser(@PathVariable Long userId) {
        return adminService.unlockUser(userId);
    }
}
