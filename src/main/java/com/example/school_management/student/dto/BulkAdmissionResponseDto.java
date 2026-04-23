package com.example.school_management.student.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkAdmissionResponseDto {
    private int totalRows;
    private int successCount;
    private int skippedCount;
    private List<String> errors; // e.g. "Row 3: Aadhar already exists"
    private List<StudentResponseDto> admittedStudents;
}