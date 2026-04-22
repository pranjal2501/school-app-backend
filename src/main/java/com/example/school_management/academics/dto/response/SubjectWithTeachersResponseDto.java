package com.example.school_management.academics.dto.response;

import com.example.school_management.common.enums.SubjectType;
import com.example.school_management.teacher.dto.response.TeacherResponseDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SubjectWithTeachersResponseDto {
    private Long subjectId;
    private String subjectName;
    private String subjectCode;
    private SubjectType subjectType;
    private Integer theoryMarks;
    private Integer practicalMarks;
    private Integer totalMarks;
    private Integer passingMarks;
    private Integer weeklyHours;

    // Contains all distinct teachers currently teaching this subject in this standard
    private List<TeacherResponseDto> assignedTeachers;
}