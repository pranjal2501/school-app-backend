package com.example.school_management.teacher.dto;

import com.example.school_management.complaint.dto.ComplaintResponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class TeacherDashboardResponseDto {
    private Float attendancePercent;
    private Integer activeAssignments;
    private Integer openComplaints;
    private Integer subjectsTaught;
    private List<ComplaintResponseDto> complaints;
}
