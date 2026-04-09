package com.example.school_management.admin;

import com.example.school_management.admin.dto.AdminRequestDto;
import com.example.school_management.admin.dto.AdminResponseDto;

import java.util.List;

public interface AdminService {
    AdminResponseDto createAdmin(AdminRequestDto request);
    List<AdminResponseDto> getAllAdmins();
    AdminResponseDto getAdminById(Long id);
}