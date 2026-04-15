package com.example.school_management.student.service;

import com.example.school_management.student.dto.*;
import jakarta.validation.Valid;

import java.util.List;

public interface StudentService {

    StudentResponseDto admitStudent(StudentFromAdminRequestDto request);

    List<StudentResponseDto> admitStudentsBulk(List<StudentFromAdminRequestDto> requests);

    List<StudentResponseDto> getAllStudents();

    StudentResponseDto getStudentById(Long id);

    StudentResponseDto updateStudent(Long id, StudentRequestDto request);

    void deleteStudent(Long id);

    StudentResponseDto updateStudentAfterAdmit(Long id, @Valid StudentRemainingInfoDto request);

    List<StudentDropdownDto> searchStudents(String standard, String division, String query);

    List<StudentResponseDto> assignClassAndDivision(StudentClassAssignmentRequestDto request);
}