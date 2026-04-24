package com.example.school_management.attendance.dto.config;

import com.example.school_management.common.enums.attendance.AttendanceType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AttendanceConfigRequestDto {

    @NotBlank(message = "Academic year is required")
    private String academicYear;

    @Min(value = 1, message = "Student working days must be at least 1")
    private Integer studentWorkingDays;

    @DecimalMin(value = "0.0", message = "Min attendance cannot be negative")
    @DecimalMax(value = "100.0", message = "Min attendance cannot exceed 100")
    private Float studentMinAttendancePct;

}
