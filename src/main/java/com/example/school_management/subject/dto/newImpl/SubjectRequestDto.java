package com.example.school_management.subject.dto.newImpl;

import com.example.school_management.common.enums.SubjectType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class SubjectRequestDto {
    @NotBlank
    private String subjectName;
    private String standard;
    @Min(1)
    private Integer weeklyHours;
    private SubjectType subjectType;

    // Marks - Can be null if creating an aggregate
    private Integer theoryMarks = 0;
    private Integer internalMarks = 0;
    private Integer practicalMarks = 0;
    private Integer projectMarks = 0;
    private Integer oralMarks = 0;
    private Integer totalMarks = 0;
    private Integer passingMarks = 0;

    // Hierarchy Flags
    private boolean isAggregateOnly;
    private Long parentSubjectId; // Null if it's a top-level subject
}

