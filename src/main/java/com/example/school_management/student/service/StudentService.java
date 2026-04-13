package com.example.school_management.student.service;

import com.example.school_management.common.enums.Division;
import com.example.school_management.common.enums.Standard;
import com.example.school_management.student.dto.*;
import jakarta.validation.Valid;

import java.util.List;

public interface StudentService {

    StudentResponseDto admitStudent(StudentFromAdminRequestDto request, String adminName);

    List<StudentResponseDto> admitStudentsBulk(List<StudentFromAdminRequestDto> requests, String adminName);

    List<StudentResponseDto> getAllStudents();

    StudentResponseDto getStudentById(Long id);

    StudentResponseDto updateStudent(Long id, StudentRequestDto request);

    void deleteStudent(Long id);

    StudentResponseDto updateStudentAfterAdmit(Long id, @Valid StudentRemainingInfoDto request);

    List<StudentDropdownDto> searchStudents(Standard standard, Division division, String query);

    List<StudentResponseDto> assignClassAndDivision(StudentClassAssignmentRequestDto request);
}