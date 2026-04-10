package com.example.school_management.notify.mapper;

import com.example.school_management.notify.dto.ParentNotificationResponseDto;
import com.example.school_management.notify.dto.TeacherNotificationResponseDto;
import com.example.school_management.notify.entity.UserNotification;
import org.springframework.stereotype.Component;

@Component
public class UserNotificationMapper {

    public ParentNotificationResponseDto toParentResponseDto(UserNotification un) {
        ParentNotificationResponseDto dto = new ParentNotificationResponseDto();
        dto.setId(un.getId());
        dto.setTitle(un.getNotification().getTitle());
        dto.setShortInfo(un.getNotification().getShortInfo());
        dto.setNotificationType(un.getNotification().getNotificationType());
        dto.setEventDateTime(un.getNotification().getEventDateTime());
        dto.setIsRead(un.getIsRead());
        dto.setReadAt(un.getReadAt());
        return dto;
    }

    public TeacherNotificationResponseDto toTeacherResponseDto(UserNotification un) {
        TeacherNotificationResponseDto dto = new TeacherNotificationResponseDto();
        dto.setId(un.getId());
        dto.setTitle(un.getNotification().getTitle());
        dto.setDescription(un.getNotification().getDescription());
        dto.setShortInfo(un.getNotification().getShortInfo());
        dto.setNotificationType(un.getNotification().getNotificationType());
        dto.setEventDateTime(un.getNotification().getEventDateTime());
        dto.setIsRead(un.getIsRead());
        dto.setReadAt(un.getReadAt());
        return dto;
    }
}
