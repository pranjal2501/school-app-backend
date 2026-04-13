package com.example.school_management.teacher;

import com.example.school_management.teacher.dto.TeacherFromAdminRequestDto;
import com.example.school_management.teacher.dto.TeacherRemainingInfoDto;
import com.example.school_management.teacher.dto.TeacherResponseDto;

import java.util.List;

public interface TeacherService {
    TeacherResponseDto onboardTeacher(TeacherFromAdminRequestDto request);
    List<TeacherResponseDto> onboardTeachersBulk(List<TeacherFromAdminRequestDto> requests);
    List<TeacherResponseDto> getAllTeachers();
    TeacherResponseDto updateTeacherAfterOnboard(Long teacherId, TeacherRemainingInfoDto request);
}