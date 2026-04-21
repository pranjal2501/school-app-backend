package com.example.school_management.teacher.controller;

import com.example.school_management.common.ApiResponse;
import com.example.school_management.notify.dto.CreateNotificationRequestDto;
import com.example.school_management.notify.dto.NotificationResponseDto;
import com.example.school_management.teacher.dto.TeacherDashboardResponseDto;
import com.example.school_management.teacher.service.TeacherService;
import com.example.school_management.teacher.dto.TeacherFromAdminRequestDto;
import com.example.school_management.teacher.dto.TeacherRemainingInfoDto;
import com.example.school_management.teacher.dto.TeacherResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
            @Valid @RequestBody TeacherFromAdminRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        TeacherResponseDto response = teacherService.onboardTeacher(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Teacher created successfully", response));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/bulk-onboard")
    public ResponseEntity<ApiResponse<List<TeacherResponseDto>>> onboardTeachersBulk(
            @Valid @RequestBody List<TeacherFromAdminRequestDto> requests,
            @AuthenticationPrincipal UserDetails userDetails) {

        List<TeacherResponseDto> responses = teacherService.onboardTeachersBulk(requests, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Teachers successfully onboarded in bulk", responses));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<TeacherResponseDto>>> getAllTeachers(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<TeacherResponseDto> response = teacherService.getAllTeachers(userDetails.getUsername());

        return ResponseEntity.ok(
                ApiResponse.ok("Teachers fetched successfully", response)
        );
    }

    @PutMapping("/{id}/details")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<TeacherResponseDto>> updateTeacherAfterOnboard(
            @PathVariable Long id,
            @Valid @RequestBody TeacherRemainingInfoDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        TeacherResponseDto response = teacherService.updateTeacherAfterOnboard(id, request, userDetails.getUsername());
        return ResponseEntity.ok(
                ApiResponse.ok("Teacher details updated successfully", response)
        );
    }

    @PostMapping("/{classId}/notify")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<NotificationResponseDto>> notifyParentsOfClass(
            @PathVariable Long classId,
            @Valid @RequestBody CreateNotificationRequestDto notification
            ) throws AccessDeniedException, java.nio.file.AccessDeniedException {
        NotificationResponseDto response = teacherService.notifyParentsOfClass(classId, notification);

        return ResponseEntity.ok(ApiResponse.ok("Notification Sent", response));
    }

    @GetMapping("/dashboard/{teacherId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<TeacherDashboardResponseDto>> getDashboard(@PathVariable Long teacherId)
    {

        return ResponseEntity.ok(ApiResponse.ok("Dashboard fetched successfully", teacherService.getDashboard(teacherId)));
    }

}