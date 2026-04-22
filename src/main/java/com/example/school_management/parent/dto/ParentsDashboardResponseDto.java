package com.example.school_management.parent.dto;

import com.example.school_management.notify.dto.ParentNotificationResponseDto;
import com.example.school_management.notify.entity.Notification;
import com.example.school_management.schoolClass.entity.SchoolClass;
import lombok.*;

import java.util.List;


@Getter
@AllArgsConstructor
@Builder
public class ParentsDashboardResponseDto {

    private String studentName;
    private String schoolClass;
    private Float attendancePercent;
    private Integer pendingAssignments;

    private List<ParentNotificationResponseDto> recentNotifications;

}
