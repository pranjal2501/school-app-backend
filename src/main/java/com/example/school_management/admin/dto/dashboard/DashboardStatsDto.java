package com.example.school_management.admin.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {
    private Long totalStudents;
    private Double studentGrowthPercent;

    private Long totalTeachers;
    private Double teacherGrowthPercent;

    private Long totalClasses;
    private Double classGrowthPercent;

    private Float avgAttendance;
    private Double attendanceGrowthPercent;
}