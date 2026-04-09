package com.example.school_management.service.Impl;

import com.example.school_management.mapper.AdminMapper;
import com.example.school_management.dto.AdminRequestDto;
import com.example.school_management.dto.AdminResponseDto;
import com.example.school_management.entity.Admin;
import com.example.school_management.exception.DuplicateResourceException;
import com.example.school_management.exception.ResourceNotFoundException;
import com.example.school_management.exception.UnauthorizedAccessException;
import com.example.school_management.repo.AdminRepository;
import com.example.school_management.service.AdminService;
import lombok.RequiredArgsConstructor;
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

    private void verifyAdminIdentity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("Authentication required to perform this action");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new UnauthorizedAccessException("Identity Verification Failed: Only administrators can execute this operation");
        }
    }

    @Override
    @Transactional
    public AdminResponseDto createAdmin(AdminRequestDto request) {
        verifyAdminIdentity();

        if (adminRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Admin", "username", request.getUsername());
        }

        Admin admin = adminMapper.toEntity(request);
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setActive(true);

        return adminMapper.toResponseDto(adminRepository.save(admin));
    }

    @Override
    public List<AdminResponseDto> getAllAdmins() {
        verifyAdminIdentity();
        return adminMapper.toResponseDtoList(adminRepository.findAll());
    }

    @Override
    public AdminResponseDto getAdminById(Long id) {
        verifyAdminIdentity();

        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "id", id));

        return adminMapper.toResponseDto(admin);
    }
}