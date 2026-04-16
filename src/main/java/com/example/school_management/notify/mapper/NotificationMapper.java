package com.example.school_management.notify.mapper;

import com.example.school_management.notify.dto.CreateNotificationRequestDto;
import com.example.school_management.notify.dto.NotificationResponseDto;
import com.example.school_management.notify.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public Notification toEntity(CreateNotificationRequestDto dto) {
        if (dto == null) {
            return null;
        }

        return Notification.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .shortInfo(dto.getShortInfo())
                .audienceType(dto.getAudienceType())
                .notificationType(dto.getNotificationType())
                .eventDateTime(dto.getEventDateTime())
                .build();
    }

    public NotificationResponseDto toResponseDto(Notification notification) {
        if (notification == null) {
            return null;
        }

        return NotificationResponseDto.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .description(notification.getDescription())
                .shortInfo(notification.getShortInfo())
                .audienceType(notification.getAudienceType())
                .notificationType(notification.getNotificationType())
                .eventDateTime(notification.getEventDateTime())
                .scheduledDispatchTime(notification.getScheduledDispatchTime())
                .dispatched(notification.getDispatched())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}