package com.example.school_management.teacher.controller;

import com.example.school_management.assignment.dto.response.TeacherClassDropdownDto;
import com.example.school_management.common.ApiResponse;
import com.example.school_management.teacher.dto.response.MyClassResponseDto;
import com.example.school_management.notify.dto.CreateNotificationRequestDto;
import com.example.school_management.notify.dto.NotificationResponseDto;
import com.example.school_management.teacher.dto.TeacherDashboardResponseDto;
import com.example.school_management.teacher.service.TeacherService;
import com.example.school_management.teacher.dto.TeacherFromAdminRequestDto;
import com.example.school_management.teacher.dto.TeacherRemainingInfoDto;
import com.example.school_management.teacher.dto.response.TeacherResponseDto;
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

    // 1. Get specific teacher (Admin or the Teacher themselves)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<TeacherResponseDto>> getTeacherById(@PathVariable Long id) {
        TeacherResponseDto response = teacherService.getTeacherById(id);
        return ResponseEntity.ok(ApiResponse.ok("Teacher details fetched successfully", response));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<TeacherResponseDto>> getCurrentTeacherProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        // userDetails.getUsername() holds the identifier (email/mobile) from the JWT
        TeacherResponseDto response = teacherService.getTeacherByIdentifier(userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.ok("Your profile fetched successfully", response));
    }

    // 2. Get all teachers (Admin strictly)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TeacherResponseDto>>> getAllTeachers() {
        List<TeacherResponseDto> responses = teacherService.getAllTeachers();
        return ResponseEntity.ok(ApiResponse.ok("All teachers fetched successfully", responses));
    }

    // Fetch all teachers not assigned as a Class Teacher
    @GetMapping("/unassigned")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TeacherResponseDto>>> getUnassignedTeachers() {

        List<TeacherResponseDto> responses = teacherService.getUnassignedTeachers();

        return ResponseEntity.ok(ApiResponse.ok("Unassigned teachers fetched successfully", responses));
    }

    @GetMapping("/my-class")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<MyClassResponseDto>> getMyClass() {
        MyClassResponseDto response = teacherService.getMyAssignedClass();
        return ResponseEntity.ok(ApiResponse.ok("Class details fetched successfully", response));
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

    @GetMapping("/my-notifications")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<NotificationResponseDto>>> getMyNotifications()
    {
        return ResponseEntity.ok(ApiResponse.ok("Notifications fetched successfully", teacherService.getMyNotifications()));

    }


    @GetMapping("/dashboard/{teacherId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<TeacherDashboardResponseDto>> getDashboard(@PathVariable Long teacherId){

        return ResponseEntity.ok(ApiResponse.ok("Dashboard fetched successfully", teacherService.getDashboard(teacherId)));
    }

    @GetMapping("/my-allocations")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<TeacherClassDropdownDto>>> getMyAllocations() {
        List<TeacherClassDropdownDto> response = teacherService.getMyAllocations();
        return ResponseEntity.ok(ApiResponse.ok("Teacher allocations fetched successfully", response));
    }

}