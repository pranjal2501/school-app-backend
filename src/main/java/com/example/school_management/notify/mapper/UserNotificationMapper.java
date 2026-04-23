package com.example.school_management.notify.mapper;

import com.example.school_management.common.enums.notify.NotificationType;
import com.example.school_management.notify.dto.ComplaintNotificationResponse;
import com.example.school_management.notify.dto.GeneralParentNotificationResponseDto;
import com.example.school_management.notify.dto.ParentNotificationResponseDto;
import com.example.school_management.notify.dto.TeacherNotificationResponseDto;
import com.example.school_management.notify.entity.Notification;
import com.example.school_management.notify.entity.UserNotification;
import org.springframework.stereotype.Component;

@Component
public class UserNotificationMapper {

//    public GeneralParentNotificationResponseDto toParentResponseDto(UserNotification un) {
//        GeneralParentNotificationResponseDto dto = new GeneralParentNotificationResponseDto();
//        dto.setId(un.getId());
//        dto.setTitle(un.getNotification().getTitle());
//        dto.setShortInfo(un.getNotification().getShortInfo());
//        dto.setNotificationType(un.getNotification().getNotificationType());
//        dto.setEventDateTime(un.getNotification().getEventDateTime());
//        dto.setIsRead(un.getIsRead());
//        dto.setReadAt(un.getReadAt());
//        return dto;
//    }

    public ParentNotificationResponseDto toParentResponseDto(UserNotification un) {
        Notification n = un.getNotification();

        if (n.getNotificationType() == NotificationType.COMPLAINT) {
            return ComplaintNotificationResponse.builder()
                    .id(un.getId())
                    .title(n.getTitle())
                    .shortInfo(n.getShortInfo())
                    .description(n.getDescription())
                    .notificationType(n.getNotificationType())
                    .eventDateTime(n.getEventDateTime())
                    .isRead(un.getIsRead())
                    .readAt(un.getReadAt())
                    .build();
        }

        return GeneralParentNotificationResponseDto.builder()
                .id(un.getId())
                .title(n.getTitle())
                .shortInfo(n.getShortInfo())
                .notificationType(n.getNotificationType())
                .eventDateTime(n.getEventDateTime())
                .urls(n.getUrls())
                .isRead(un.getIsRead())
                .readAt(un.getReadAt())
                .build();
    }

    public TeacherNotificationResponseDto toTeacherResponseDto(UserNotification un) {
        TeacherNotificationResponseDto dto = new TeacherNotificationResponseDto();
        dto.setId(un.getId());
        dto.setTitle(un.getNotification().getTitle());
        dto.setDescription(un.getNotification().getDescription());
        dto.setShortInfo(un.getNotification().getShortInfo());
        dto.setNotificationType(un.getNotification().getNotificationType());
        dto.setEventDateTime(un.getNotification().getEventDateTime());
        dto.setUrls(un.getNotification().getUrls());
        dto.setIsRead(un.getIsRead());
        dto.setReadAt(un.getReadAt());
        return dto;
    }
}
