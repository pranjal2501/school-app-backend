package com.example.school_management.attendance.service;

import com.example.school_management.attendance.dto.config.AttendanceConfigRequestDto;
import com.example.school_management.attendance.dto.config.AttendanceConfigResponseDto;
import com.example.school_management.attendance.entity.AttendanceConfig;
import com.example.school_management.attendance.mapper.ConfigMapper;
import com.example.school_management.attendance.repository.AttendanceConfigRepo;
import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.enums.Role;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class AttendanceConfigService {
    private final AttendanceConfigRepo configRepo;
    private final ConfigMapper configMapper;
    private final RoleVerifier verify;


    // Create or Update rules for the year (e.g., 220 days, 75% required)
    public AttendanceConfigResponseDto saveConfig(AttendanceConfigRequestDto request) throws AccessDeniedException {

        if(!verify.hasRole(Role.ADMIN)){
            throw new AccessDeniedException("Only ADMIN can perform this action");
        }

            AttendanceConfig config = configRepo.findByAcademicYear(request.getAcademicYear())
                    .orElse(new AttendanceConfig());

            config.setAcademicYear(request.getAcademicYear());
            config.setStudentWorkingDays(request.getStudentWorkingDays());
            config.setStudentMinAttendancePct(request.getStudentMinAttendancePct());

            return configMapper.toResponseDto(configRepo.save(config));

    }

    // Used by your AttendanceService to do calculations
    public AttendanceConfigResponseDto getConfigForYear(String academicYear) {
        AttendanceConfig conf=  configRepo.findByAcademicYear(academicYear)
                .orElseThrow(() -> new EntityNotFoundException("Attendance rules not set for year: " + academicYear));

        return configMapper.toResponseDto(conf);
    }
}
