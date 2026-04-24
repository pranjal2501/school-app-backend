package com.example.school_management.assignment.dto.response;

import com.example.school_management.common.enums.AssignmentType;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AssignmentResponseDto {
    private Long id;
    private String title;
    private String description;
    private String documentUrl;
    private AssignmentType assignmentType;
    private LocalDateTime dueDate;
    private Integer totalMarks;
    private LocalDateTime createdAt;

    // Flattened relation data for the frontend
    private Long classId;
    private String className;
    private Long subjectId;
    private String subjectName;
    private String teacherName;

    private Boolean isSubmitted;
}