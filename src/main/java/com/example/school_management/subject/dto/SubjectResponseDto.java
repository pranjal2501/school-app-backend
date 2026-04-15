package com.example.school_management.subject.dto;

// Standard Response DTO (Flattened for frontend ease)
import com.example.school_management.common.enums.SubjectType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubjectResponseDto {
    private Long id;
    private String subjectName;
    private Integer subjectCode;
    private String standard;
    private Integer weeklyHours;
    private SubjectType subjectType;
    private Long teacherId;
    private String teacherName;

    // Marking Scheme
    private Integer theoryMarks;
    private Integer internalMarks;
    private Integer practicalMarks;
    private Integer projectMarks;
    private Integer oralMarks;
    private Integer totalMarks;
    private Integer passingMarks;
}