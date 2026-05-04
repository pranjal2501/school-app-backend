package com.example.school_management.syllabus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParentSubjectDto {
    private Long id;             // Subject ID
    private String name;         // Subject Name
    private Long allocationId;
}