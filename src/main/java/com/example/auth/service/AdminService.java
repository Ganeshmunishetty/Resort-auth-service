package com.example.auth.service;

import com.example.auth.entity.UserCredential;

import java.util.List;

public interface AdminService {
    
    List<UserCredential> getPendingApprovals();
    
    String approveUser(Long userId);
    
    String rejectUser(Long userId);
    
    List<UserCredential> getAllUsers();
    
    String unlockUser(Long userId);
}
