package com.example.school_management.teacher.service;

import com.example.school_management.notify.dto.CreateNotificationRequestDto;
import com.example.school_management.notify.dto.NotificationResponseDto;
import com.example.school_management.teacher.dto.TeacherDashboardResponseDto;
import com.example.school_management.teacher.dto.TeacherFromAdminRequestDto;
import com.example.school_management.teacher.dto.TeacherRemainingInfoDto;
import com.example.school_management.teacher.dto.response.MyClassResponseDto;
import com.example.school_management.teacher.dto.response.TeacherResponseDto;
import jakarta.validation.Valid;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface TeacherService {
    TeacherResponseDto onboardTeacher(TeacherFromAdminRequestDto request, String username);
    List<TeacherResponseDto> onboardTeachersBulk(List<TeacherFromAdminRequestDto> requests, String username);
    TeacherResponseDto updateTeacherAfterOnboard(Long teacherId, TeacherRemainingInfoDto request, String username);
    TeacherResponseDto getTeacherById(Long id);
    List<TeacherResponseDto> getAllTeachers();
    MyClassResponseDto getMyAssignedClass();
    TeacherResponseDto getTeacherByIdentifier(String username);
    List<TeacherResponseDto> getUnassignedTeachers();

    NotificationResponseDto notifyParentsOfClass(long classId, @Valid CreateNotificationRequestDto notification) throws AccessDeniedException;

    TeacherDashboardResponseDto getDashboard(Long teacherId);

    List<NotificationResponseDto> getMyNotifications();
}