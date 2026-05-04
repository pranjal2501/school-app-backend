package com.example.school_management.parent.dto;

import com.example.school_management.notify.dto.ParentNotificationResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParentsDashboardResponseDto {
    private Long studentId;
    private String parentName;
    private String studentName;
    private String schoolClass;

    private Long classTeacherId;
    private String classTeacherName;
    private String parentMobile;

    private Boolean isClassAllocated;
    private Boolean isProfileComplete;

    private Float attendancePercent;
    private Integer pendingAssignments;
    private List<ParentNotificationResponseDto> recentNotifications;
}