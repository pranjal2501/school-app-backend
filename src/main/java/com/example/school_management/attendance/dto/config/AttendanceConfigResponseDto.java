package com.example.school_management.attendance.dto.config;

import com.example.school_management.common.enums.attendance.AttendanceType;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class AttendanceConfigResponseDto{
        private Long id;
        private String academicYear;
        private Integer studentWorkingDays;
        private Float studentMinAttendancePct;
}
