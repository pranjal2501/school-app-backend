package com.example.school_management.subject.dto;

import com.example.school_management.common.enums.SubjectType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubjectRequestDto {

    @NotBlank(message = "Subject name cannot be blank")
    private String subjectName;

    @NotNull(message = "Subject code is required")
    private Integer subjectCode;

    @NotNull(message = "Weekly hours are required")
    @Min(value = 1, message = "Weekly hours must be at least 1")
    private Integer weeklyHours;

    @NotNull(message = "Subject type (COMPULSORY/OPTIONAL) is required")
    private SubjectType subjectType;

    // Optional: A school might create the subject before hiring the teacher
    private Long subjectTeacherId;

    // Defaulting to 0 prevents NullPointerExceptions during our strict math validations
    @NotNull(message = "Theory marks cannot be null, send 0 if not applicable")
    @Min(0)
    private Integer theoryMarks = 0;

    @NotNull
    @Min(0)
    private Integer internalMarks = 0;

    @NotNull
    @Min(0)
    private Integer practicalMarks = 0;

    @NotNull
    @Min(0)
    private Integer projectMarks = 0;

    @NotNull
    @Min(0)
    private Integer oralMarks = 0;

    @NotNull(message = "Total marks are strictly required")
    @Min(value = 1, message = "Total marks must be greater than 0")
    private Integer totalMarks;

    @NotNull(message = "Passing marks are strictly required")
    @Min(0)
    private Integer passingMarks;
}