package com.example.school_management.service;

import com.example.school_management.dto.ClassRequestDto;
import com.example.school_management.dto.ClassResponseDto;
import java.util.List;

public interface ClassService {
    ClassResponseDto createClass(ClassRequestDto request);
    List<ClassResponseDto> getAllClasses();
    ClassResponseDto getClassById(Long id);
    ClassResponseDto updateClass(Long id, ClassRequestDto request);
    void deleteClass(Long id);
    ClassResponseDto assignClassTeacher(Long classId, Long teacherId);
}