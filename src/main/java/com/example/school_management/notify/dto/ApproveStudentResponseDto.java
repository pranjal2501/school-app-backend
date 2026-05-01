package com.example.school_management.notify.dto;

import com.example.school_management.common.enums.notify.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class ApproveStudentResponseDto implements ParentNotificationResponseDto{

    private Long id;
    private String title;
    private String shortInfo;
    private String description;
    private NotificationType notificationType;
    private LocalDateTime expiresAt;

    private Boolean isRead;
    private LocalDateTime readAt;

    @Builder.Default
    private String approveLink= "{{local_base_url}}/api/v1/students/approve/";

    @Builder.Default
    private String rejectLink= "{{local_base_url}}/api/v1/students/reject/";
}
