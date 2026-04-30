package com.example.school_management.admin.mapper;

import com.example.school_management.admin.dto.dashboard.DashboardStatsDto;
import com.example.school_management.admin.dto.dashboard.TodayClassDto;
import com.example.school_management.notify.dto.NotificationResponseDto;
import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.teacher.entity.Teacher;
import com.example.school_management.admin.dto.dashboard.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminDashboardMapper {

    public TodayClassDto toTodayClassDto(
            SchoolClass schoolClass,
            int totalStudents,
            Integer totalPresent,
            boolean attendanceMarked) {

        Teacher classTeacher = schoolClass.getClassTeacher();
        String teacherName = classTeacher != null
                ? classTeacher.getFirstName() + " " + classTeacher.getLastName()
                : "Not Assigned";

        return TodayClassDto.builder()
                .className(schoolClass.getClassName())
                .classTeacherName(teacherName)
                .totalStudents(totalStudents)
                .totalPresent(totalPresent)
                .attendanceMarked(attendanceMarked)
                .build();
    }

    public DashboardStatsDto toStatsDto(
            long totalStudents, double studentGrowth,
            long totalTeachers, double teacherGrowth,
            long totalClasses, double classGrowth,
            Float avgAttendance, Double attendanceGrowth) {

        return DashboardStatsDto.builder()
                .totalStudents(totalStudents)
                .studentGrowthPercent(studentGrowth)
                .totalTeachers(totalTeachers)
                .teacherGrowthPercent(teacherGrowth)
                .totalClasses(totalClasses)
                .classGrowthPercent(classGrowth)
                .avgAttendance(avgAttendance)
                .attendanceGrowthPercent(attendanceGrowth)
                .build();
    }

    public AdminDashboardResponseDto toDashboardResponseDto(
            DashboardStatsDto stats,
            List<TodayClassDto> todayClasses,
            List<NotificationResponseDto> importantAlerts) {

        return AdminDashboardResponseDto.builder()
                .stats(stats)
                .todayClasses(todayClasses)
                .importantAlerts(importantAlerts)
                .build();
    }
}
