package com.example.school_management.student;

import com.example.school_management.student.dto.StudentClassAssignmentRequestDto;
import com.example.school_management.student.dto.StudentRequestDto;
import com.example.school_management.student.dto.StudentResponseDto;

import java.util.List;

public interface StudentService {

    StudentResponseDto admitStudent(StudentRequestDto request);

    List<StudentResponseDto> admitStudentsBulk(List<StudentRequestDto> requests);

    List<StudentResponseDto> getAllStudents();

    StudentResponseDto getStudentById(Long id);

    StudentResponseDto getStudentByRollNumber(Integer rollNumber);

    StudentResponseDto updateStudent(Long id, StudentRequestDto request);

    void toggleStudentStatus(Long id);

    void deleteStudent(Long id);

    StudentResponseDto assignClassAndDivision(Long id, StudentClassAssignmentRequestDto request);
}