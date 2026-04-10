package com.example.school_management.notify.dto;

import com.example.school_management.common.enums.notify.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ParentNotificationResponseDto {
    private Long id;   //usernotification_id
    private String title;
    private String shortInfo;
    private NotificationType notificationType;
    private LocalDateTime eventDateTime;
    private Boolean isRead;
    private LocalDateTime readAt;
}