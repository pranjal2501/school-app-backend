package com.example.school_management.assignment.service;


import com.example.school_management.academics.dto.response.AssignmentResponseDto;
import com.example.school_management.assignment.dto.request.AssignmentRequestDto;
import com.example.school_management.assignment.dto.response.TeacherClassDropdownDto;

import java.util.List;

public interface AssignmentService {
    List<TeacherClassDropdownDto> getMyAllocations();
    AssignmentResponseDto createAssignment(AssignmentRequestDto request);
    List<AssignmentResponseDto> getAssignmentsForClass(Long classId);
}