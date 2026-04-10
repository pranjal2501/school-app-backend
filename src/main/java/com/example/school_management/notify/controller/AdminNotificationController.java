package com.example.school_management.notify.controller;
// Admin creates and views notification


import com.example.school_management.common.ApiResponse;
import com.example.school_management.notify.dto.CreateNotificationRequestDto;
import com.example.school_management.notify.dto.NotificationResponseDto;
import com.example.school_management.notify.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminNotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<ApiResponse<NotificationResponseDto>> createNotification(
            @Valid @RequestBody CreateNotificationRequestDto request) {
        NotificationResponseDto response = notificationService.createNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Notification created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponseDto>>> getAllNotifications() {
        List<NotificationResponseDto> response = notificationService.getAllNotifications();
        return ResponseEntity.ok(ApiResponse.ok("Notifications fetched successfully", response));
    }
}