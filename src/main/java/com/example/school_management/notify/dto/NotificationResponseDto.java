package com.example.school_management.notify.dto;

import com.example.school_management.common.enums.notify.AudienceType;
import com.example.school_management.common.enums.notify.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class NotificationResponseDto {
    private Long id;
    private String title;
    private String description;
    private String shortInfo;
    private AudienceType audienceType;
    private NotificationType notificationType;
    private List<String> urls;
    private LocalDateTime eventDateTime;
    private LocalDateTime scheduledDispatchTime;
    private Boolean dispatched;
    private LocalDateTime createdAt;
}