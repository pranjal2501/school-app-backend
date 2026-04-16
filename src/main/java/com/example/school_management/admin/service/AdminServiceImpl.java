package com.example.school_management.admin.service;

import com.example.school_management.admin.mapper.AdminMapper;
import com.example.school_management.admin.dto.AdminRequestDto;
import com.example.school_management.admin.dto.AdminResponseDto;
import com.example.school_management.admin.entity.Admin;
import com.example.school_management.admin.repository.AdminRepository;
import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.enums.Role;
import com.example.school_management.common.exception.DuplicateResourceException;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.common.exception.UnauthorizedAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // Automatically generates a constructor for all 'final' fields
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminMapper adminMapper;
    private final RoleVerifier roleVerifier;

    private void verifyAndGetAdminRecord() {
        if (!roleVerifier.hasRole(Role.ADMIN)) {
            throw new org.springframework.security.access.AccessDeniedException("Access Denied: Only Admins can perform this action.");
        }
        adminRepository.findByUsername(roleVerifier.getCurrentUsername())
                .orElseThrow(() -> new AccessDeniedException("Admin record not found."));
    }

    @Override
    @Transactional
    public AdminResponseDto createAdmin(AdminRequestDto request) {
        verifyAndGetAdminRecord();

        Admin admin = adminMapper.toEntity(request);
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setActive(true);

        return adminMapper.toResponseDto(adminRepository.save(admin));
    }

    @Override
    public List<AdminResponseDto> getAllAdmins() {
        verifyAndGetAdminRecord();
        return adminMapper.toResponseDtoList(adminRepository.findAll());
    }

    @Override
    public AdminResponseDto getAdminById(Long id) {
        verifyAndGetAdminRecord();

        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "id", id));

        return adminMapper.toResponseDto(admin);
    }
}