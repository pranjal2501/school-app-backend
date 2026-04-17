package com.example.school_management.assignment.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class TeacherClassDropdownDto {
    private Long classId;
    private String className;
    // Nested list of subjects for this specific class
    private List<TeacherSubjectDropdownDto> subjects;
}