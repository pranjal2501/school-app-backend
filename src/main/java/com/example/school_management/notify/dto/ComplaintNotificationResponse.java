package com.example.school_management.notify.dto;

import com.example.school_management.common.enums.notify.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ComplaintNotificationResponse implements ParentNotificationResponseDto {
    private Long id;
    private String title;
    private String shortInfo;
    private String description;
    private NotificationType notificationType;
    private LocalDateTime eventDateTime;
    private Boolean isRead;
    private LocalDateTime readAt;
}
