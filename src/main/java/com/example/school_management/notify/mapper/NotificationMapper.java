package com.example.school_management.notify.mapper;

import com.example.school_management.attendance.repository.SchoolHolidaysRepository;
import com.example.school_management.notify.dto.CreateNotificationRequestDto;
import com.example.school_management.notify.dto.NotificationResponseDto;
import com.example.school_management.notify.entity.Notification;
import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.schoolClass.repo.SchoolClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class NotificationMapper {

    private final SchoolClassRepository schoolClassRepository;

    public Notification toEntity(CreateNotificationRequestDto dto) {
        if (dto == null) {
            return null;
        }

        Set<SchoolClass> targetClasses = new HashSet<>();
        if (dto.getClassId() != null && !dto.getClassId().isEmpty()) {
            for (Long classId : dto.getClassId()) {
                targetClasses.add(schoolClassRepository.getReferenceById(classId));
            }
        }
        return Notification.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .shortInfo(dto.getShortInfo())
                .audienceType(dto.getAudienceType())
                .notificationType(dto.getNotificationType())
                .eventDateTime(dto.getEventDateTime())
                .scheduledDispatchTime(dto.getDispatchNow() ? LocalDateTime.now() : null)
                .dispatched(false)
                .targetClasses(targetClasses)
                .teacherIds(dto.getTeacherId())
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