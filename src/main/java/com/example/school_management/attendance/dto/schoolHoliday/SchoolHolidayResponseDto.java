package com.example.school_management.attendance.dto.schoolHoliday;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SchoolHolidayResponseDto {
    private Long id;
    private LocalDate holidayDate;
    private String description;
    private String academicYear;
    private String dayOfWeek;
}
