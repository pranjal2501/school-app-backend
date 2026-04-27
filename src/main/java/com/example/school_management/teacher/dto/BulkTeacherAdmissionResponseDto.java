package com.example.school_management.teacher.dto;

import com.example.school_management.student.dto.StudentResponseDto;
import com.example.school_management.teacher.dto.response.TeacherResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkTeacherAdmissionResponseDto {
    private int totalRows;
    private int successCount;
    private int skippedCount;
    private List<String> errors;
    private List<TeacherResponseDto> admittedTeachers;
}
