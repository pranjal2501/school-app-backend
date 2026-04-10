package com.example.school_management.student;

import com.example.school_management.common.enums.Division;
import com.example.school_management.common.enums.Standard;
import com.example.school_management.student.dto.*;
import jakarta.validation.Valid;

import java.util.List;

public interface StudentService {

    StudentResponseDto admitStudent(StudentFromAdminRequestDto request);

    List<StudentResponseDto> admitStudentsBulk(List<StudentFromAdminRequestDto> requests);

    List<StudentResponseDto> getAllStudents();

    StudentResponseDto getStudentById(Long id);

    StudentResponseDto getStudentByRollNumber(Integer rollNumber);

    StudentResponseDto updateStudent(Long id, StudentRequestDto request);

    void toggleStudentStatus(Long id);

    void deleteStudent(Long id);

    StudentResponseDto assignClassAndDivision(Long id, StudentClassAssignmentRequestDto request);

    StudentResponseDto updateStudentAfterAdmit(Long id, @Valid StudentRemainingInfoDto request);

    List<StudentDropdownDto> searchStudents(Standard standard, Division division, String query);
}