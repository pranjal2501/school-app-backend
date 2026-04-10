package com.example.school_management.notify.mapper;

import com.example.school_management.notify.dto.CreateNotificationRequestDto;
import com.example.school_management.notify.dto.NotificationResponseDto;
import com.example.school_management.notify.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public Notification toEntity(CreateNotificationRequestDto dto) {
        Notification notification = new Notification();
        notification.setTitle(dto.getTitle());
        notification.setDescription(dto.getDescription());
        notification.setShortInfo(dto.getShortInfo());
        notification.setAudienceType(dto.getAudienceType());
        notification.setNotificationType(dto.getNotificationType());
        notification.setEventDateTime(dto.getEventDateTime());
        return notification;
    }

    public NotificationResponseDto toResponseDto(Notification notification) {
        NotificationResponseDto dto = new NotificationResponseDto();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setDescription(notification.getDescription());
        dto.setShortInfo(notification.getShortInfo());
        dto.setAudienceType(notification.getAudienceType());
        dto.setNotificationType(notification.getNotificationType());
        dto.setEventDateTime(notification.getEventDateTime());
        dto.setScheduledDispatchTime(notification.getScheduledDispatchTime());
        dto.setDispatched(notification.getDispatched());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}