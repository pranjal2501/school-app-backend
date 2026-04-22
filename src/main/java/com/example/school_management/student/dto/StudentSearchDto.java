package com.example.school_management.student.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentSearchDto {
    private Long id;
    private String fullName;
    private String regId;
    private String className;
    private Integer rollNumber;
}