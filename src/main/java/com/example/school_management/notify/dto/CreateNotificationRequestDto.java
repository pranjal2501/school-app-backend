package com.example.school_management.notify.dto;

import com.example.school_management.common.enums.notify.*;
import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.teacher.entity.Teacher;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class CreateNotificationRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Short info is required")
    private String shortInfo;

//    @NotNull(message = "Audience type is required")
    private AudienceType audienceType;

    @NotNull(message = "Notification type is required")
    private NotificationType notificationType;

    @NotNull(message = "Event date time is required")
    @Future(message = "Event date must be in the future")
    private LocalDateTime eventDateTime;

    private Set<Long> classId;

    private Set<Long> teacherId;

    private Boolean dispatchNow;
}
