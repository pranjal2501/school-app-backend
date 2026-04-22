package com.example.school_management.notify.controller;

import com.example.school_management.common.ApiResponse;
import com.example.school_management.notify.dto.GeneralParentNotificationResponseDto;
import com.example.school_management.notify.dto.ParentNotificationResponseDto;
import com.example.school_management.notify.dto.TeacherNotificationResponseDto;
import com.example.school_management.notify.service.UserNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-notifications")
@RequiredArgsConstructor
public class UserNotificationController {

    private final UserNotificationService userNotificationService;

    @GetMapping("/parent/{parentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PARENT')")
    public ResponseEntity<ApiResponse<List<ParentNotificationResponseDto>>> getParentNotifications(
            @PathVariable Long parentId) {
        List<ParentNotificationResponseDto> response = userNotificationService.getParentNotifications(parentId);
        return ResponseEntity.ok(ApiResponse.ok("Notifications fetched successfully", response));
    }

    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<TeacherNotificationResponseDto>>> getTeacherNotifications(
            @PathVariable Long teacherId) {
        List<TeacherNotificationResponseDto> response = userNotificationService.getTeacherNotifications(teacherId);
        return ResponseEntity.ok(ApiResponse.ok("Notifications fetched successfully", response));
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('PARENT', 'TEACHER')")
    public ResponseEntity<ApiResponse<String>> markAsRead(@PathVariable Long id) {
        userNotificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.ok("Notification marked as read", null));
    }
}