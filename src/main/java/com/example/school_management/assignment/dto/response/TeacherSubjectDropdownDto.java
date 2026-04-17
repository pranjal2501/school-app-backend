package com.example.school_management.assignment.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeacherSubjectDropdownDto {
    private Long subjectId;
    private String subjectName;
}