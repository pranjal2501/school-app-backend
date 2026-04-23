package com.example.school_management.attendance.dto.schoolHoliday;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SchoolHolidayRequestDto {
    @NotNull(message = "Holiday date is required")
    private LocalDate holidayDate;

    @NotBlank(message = "Holiday description is required (e.g., Christmas)")
    private String description;
}
