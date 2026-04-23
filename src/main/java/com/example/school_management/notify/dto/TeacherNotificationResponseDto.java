package com.example.school_management.notify.dto;

import com.example.school_management.common.enums.notify.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TeacherNotificationResponseDto {
    private Long id; //usernotification_id
    private String title;
    private String description;  // ← full details
    private String shortInfo;
    private NotificationType notificationType;
    private List<String> urls;
    private LocalDateTime eventDateTime;
    private Boolean isRead;
    private LocalDateTime readAt;
}