package com.example.school_management.dto;

import com.example.school_management.enums.Division;
import com.example.school_management.enums.Standard;
import lombok.Data;

@Data
public class ClassResponseDto {
    private Long id;
    private Standard standard;
    private Division division;
    private Integer totalStudents;
    private Integer boys;
    private Integer girls;
    private String className;

    // Extracted safely from the Teacher entity
    private Long classTeacherId;
    private String classTeacherName;
}