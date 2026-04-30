package com.example.school_management.admin.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodayClassDto {
    private String className;
    private String classTeacherName;
    private Integer totalStudents;
    private Integer totalPresent;  // null if attendance not marked yet
    private Boolean attendanceMarked;
}