package com.example.school_management.admin;

import com.example.school_management.admin.dto.AdminRequestDto;
import com.example.school_management.admin.dto.AdminResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdminMapper {

    public Admin toEntity(AdminRequestDto dto) {
        Admin admin = new Admin();
        admin.setUsername(dto.getUsername());
        admin.setPassword(dto.getPassword());
        return admin;
    }

    public AdminResponseDto toResponseDto(Admin admin) {
        return AdminResponseDto.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .isActive(admin.isActive())
                .build();
    }

    public List<AdminResponseDto> toResponseDtoList(List<Admin> admins) {
        return admins.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }
}