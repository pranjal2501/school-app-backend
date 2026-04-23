package com.example.school_management.attendance.dto.config;

import com.example.school_management.common.enums.attendance.AttendanceType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AttendanceConfigRequestDto {

    @NotBlank(message = "Academic year is required")
    private String academicYear;

    @NotNull(message = "Attendance type is required")
    private AttendanceType attendanceType;

    @Min(value = 1, message = "Student working days must be at least 1")
    private Integer studentWorkingDays;
    private Boolean allowLateMarks;

    @Min(value = 0)
    private Integer lateMarksWindow;

    private Boolean notifyParentOnAbsent;

    @Min(value = 1)
    private Integer absentNotifyThreshold;

    @Min(value = 0)
    private Integer minLeaveNoticeDays;
}
