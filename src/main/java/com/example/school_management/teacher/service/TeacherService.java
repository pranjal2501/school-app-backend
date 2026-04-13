package com.example.school_management.teacher.service;

import com.example.school_management.teacher.dto.TeacherFromAdminRequestDto;
import com.example.school_management.teacher.dto.TeacherRemainingInfoDto;
import com.example.school_management.teacher.dto.TeacherResponseDto;

import java.util.List;

public interface TeacherService {
    TeacherResponseDto onboardTeacher(TeacherFromAdminRequestDto request, String username);
    List<TeacherResponseDto> onboardTeachersBulk(List<TeacherFromAdminRequestDto> requests, String username);
    List<TeacherResponseDto> getAllTeachers(String username);
    TeacherResponseDto updateTeacherAfterOnboard(Long teacherId, TeacherRemainingInfoDto request, String username);
}