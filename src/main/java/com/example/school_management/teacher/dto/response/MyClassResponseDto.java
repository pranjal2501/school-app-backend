package com.example.school_management.teacher.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MyClassResponseDto {
    private Long classId;
    private String className; // e.g., "7-A"
    private Long teacherId;
    private String teacherName;
}