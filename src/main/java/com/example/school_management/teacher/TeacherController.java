package com.example.school_management.teacher;

import com.example.school_management.common.ApiResponse;
import com.example.school_management.teacher.dto.TeacherFromAdminRequestDto;
import com.example.school_management.teacher.dto.TeacherRemainingInfoDto;
import com.example.school_management.teacher.dto.TeacherResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/onboard")
    public ResponseEntity<ApiResponse<TeacherResponseDto>> onboardTeacher(
            @Valid @RequestBody TeacherFromAdminRequestDto request) {
        TeacherResponseDto response = teacherService.onboardTeacher(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Teacher created successfully", response));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<TeacherResponseDto>>> getAllTeachers() {

        List<TeacherResponseDto> response = teacherService.getAllTeachers();

        return ResponseEntity.ok(
                ApiResponse.ok("Teachers fetched successfully", response)
        );
    }

    @PutMapping("/{id}/details")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<TeacherResponseDto>> updateTeacherAfterOnboard(
            @PathVariable Long id,
            @Valid @RequestBody TeacherRemainingInfoDto request) {
        TeacherResponseDto response = teacherService.updateTeacherAfterOnboard(id, request);
        return ResponseEntity.ok(
                ApiResponse.ok("Teacher details updated successfully", response)
        );
    }
}