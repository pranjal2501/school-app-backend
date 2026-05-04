package com.example.school_management.notify.dto;

import com.example.school_management.common.enums.FeeType;
import com.example.school_management.common.enums.notify.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialEventNotificationResponseDto implements ParentNotificationResponseDto {
    private Long id;
    private String title;
    private String shortInfo;
    private String description;
    private NotificationType notificationType= NotificationType.SPECIAL_EVENT;
    private LocalDateTime eventDateTime;
    private Double feeAmount;
    private FeeType feeType = FeeType.ACTIVITY_FEES;
    private Boolean feePaid;
    private LocalDateTime feePaidAt;
    private LocalDateTime paymentDeadline;
    private Boolean isRead;
    private LocalDateTime readAt;
}
