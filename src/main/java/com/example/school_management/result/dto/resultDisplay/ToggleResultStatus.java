package com.example.school_management.result.dto.resultDisplay;

import lombok.Data;

@Data
public class ToggleResultStatus {
    private Long examTypeId;
    private String academicYear;
    private Boolean publish;
}