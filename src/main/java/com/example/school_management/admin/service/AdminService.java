package com.example.school_management.admin.service;

import com.example.school_management.admin.dto.AdminRequestDto;
import com.example.school_management.admin.dto.AdminResponseDto;
import com.example.school_management.admin.dto.dashboard.AdminDashboardResponseDto;

import java.util.List;

public interface AdminService {
    AdminResponseDto createAdmin(AdminRequestDto request);
    List<AdminResponseDto> getAllAdmins();
    AdminResponseDto getAdminById(Long id);


    AdminDashboardResponseDto getDashboard();
}