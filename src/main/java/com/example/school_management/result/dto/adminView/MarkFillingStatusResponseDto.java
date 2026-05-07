package com.example.school_management.result.dto.adminView;

import com.example.school_management.common.enums.MarkFillingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarkFillingStatusResponseDto {
    private Long allocationId;
    private String className;
    private String subjectName;
    private String teacherName;
    private Long examTypeId;
    private String examTypeName;
    private MarkFillingStatus status;
    private int totalStudents;
    private int studentsGradedCount;
}