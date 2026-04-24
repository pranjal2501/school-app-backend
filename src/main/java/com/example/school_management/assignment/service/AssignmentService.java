package com.example.school_management.assignment.service;


import com.example.school_management.assignment.dto.request.AssignmentBulkRequestDto;
import com.example.school_management.assignment.dto.request.AssignmentRequestDto;
import com.example.school_management.assignment.dto.request.AssignmentUpdateRequestDto;
import com.example.school_management.assignment.dto.request.SubmissionBulkRequestDto;
import com.example.school_management.assignment.dto.response.AssignmentResponseDto;
import com.example.school_management.assignment.dto.response.SubmissionResponseDto;
import com.example.school_management.assignment.dto.response.TeacherClassDropdownDto;
import jakarta.validation.Valid;

import java.util.List;

public interface AssignmentService {
    List<TeacherClassDropdownDto> getMyAllocations();
    void deleteAssignment(Long id);
    AssignmentResponseDto createAssignment(AssignmentRequestDto request);
    AssignmentResponseDto updateAssignment(Long assignmentId, AssignmentUpdateRequestDto request);
    List<AssignmentResponseDto> getAssignmentsForClass(Long classId);
    List<SubmissionResponseDto> upsertSubmissions(Long assignmentId, SubmissionBulkRequestDto request);
    List<SubmissionResponseDto> getSubmissionsForAssignment(Long assignmentId);
    List<AssignmentResponseDto> createBulkAssignments(@Valid List<AssignmentBulkRequestDto> requests);
    List<AssignmentResponseDto> getMyCreatedAssignments();
    List<AssignmentResponseDto> getStudentAssignmentsWithStatus(String regId);
}