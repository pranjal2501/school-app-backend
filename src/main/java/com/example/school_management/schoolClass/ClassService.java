package com.example.school_management.schoolClass;

import com.example.school_management.schoolClass.dto.ClassRequestDto;
import com.example.school_management.schoolClass.dto.ClassResponseDto;
import com.example.school_management.schoolClass.dto.StudentRollNoResponseDto;

import java.util.List;

public interface ClassService {
    ClassResponseDto createClass(ClassRequestDto request);
    List<ClassResponseDto> getAllClasses();
    ClassResponseDto getClassById(Long id);
    ClassResponseDto updateClass(Long id, ClassRequestDto request);
    void deleteClass(Long id);
    ClassResponseDto assignClassTeacher(Long classId, Long teacherId);

    List<String> assignRollNumberToStudents(Long classId);

    List<StudentRollNoResponseDto> getSTudentListWIthRollNum(Long classId);
}