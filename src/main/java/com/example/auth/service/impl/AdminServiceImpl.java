package com.example.auth.service.impl;

import com.example.auth.entity.UserCredential;
import com.example.auth.exception.ResourceNotFoundException;
import com.example.auth.repository.UserCredentialRepository;
import com.example.auth.service.AdminService;
import com.example.auth.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private final UserCredentialRepository userRepository;
    private final EmailService emailService;

    @Override
    public List<UserCredential> getPendingApprovals() {
        return userRepository.findByStatus("PENDING");
    }

    @Override
    public String approveUser(Long userId) {
        UserCredential user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!"PENDING".equals(user.getStatus())) {
            throw new IllegalStateException("User is not pending approval");
        }

        user.setStatus("APPROVED");
        userRepository.save(user);

        // Send approval email
        emailService.sendAccountApprovalEmail(user.getEmail(), user.getUsername());

        return "User approved successfully";
    }

    @Override
    public String rejectUser(Long userId) {
        UserCredential user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!"PENDING".equals(user.getStatus())) {
            throw new IllegalStateException("User is not pending approval");
        }

        // Delete rejected user
        userRepository.delete(user);

        return "User rejected and removed";
    }

    @Override
    public List<UserCredential> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public String unlockUser(Long userId) {
        UserCredential user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.resetFailedAttempts();
        userRepository.save(user);

        return "User account unlocked successfully";
    }
}
