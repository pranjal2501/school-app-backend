package com.example.school_management.attendance.dto.latemark;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UpdateLateMarksRequestDto {

    @NotNull(message = "Academic year is required")
    private String academicYear;

    @NotNull(message = "Late roll numbers are required")
    private List<Integer> lateRollNumbers;
}
