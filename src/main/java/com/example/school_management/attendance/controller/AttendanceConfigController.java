package com.example.school_management.attendance.controller;

import com.example.school_management.attendance.dto.config.AttendanceConfigRequestDto;
import com.example.school_management.attendance.dto.config.AttendanceConfigResponseDto;
import com.example.school_management.attendance.service.AttendanceConfigService;
import com.example.school_management.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/v1/attendance-config")
@RequiredArgsConstructor
public class AttendanceConfigController {

    private final AttendanceConfigService configService;

    //Create or Update attendance settings for an academic year.
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AttendanceConfigResponseDto>> saveConfiguration(
            @Valid @RequestBody AttendanceConfigRequestDto request) throws AccessDeniedException {

        AttendanceConfigResponseDto response = configService.saveConfig(request);
        return ResponseEntity.ok(ApiResponse.ok("Configuration saved successfully", response));
    }


    //Fetch settings for a specific academic year.
    @GetMapping("/{academicYear}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AttendanceConfigResponseDto>> getConfiguration(
            @PathVariable String academicYear) {

        AttendanceConfigResponseDto response = configService.getConfigForYear(academicYear);
        return ResponseEntity.ok(ApiResponse.ok("Configuration fetched successfully", response));
    }
}
