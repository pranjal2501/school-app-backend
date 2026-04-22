package com.example.school_management.notify.service;

import com.example.school_management.notify.dto.GeneralParentNotificationResponseDto;
import com.example.school_management.notify.dto.ParentNotificationResponseDto;
import com.example.school_management.notify.dto.TeacherNotificationResponseDto;

import java.util.List;

public interface UserNotificationService {
    // dispatch notifications to users
    void dispatchNotifications();  // called by scheduler

    // parent fetches their notifications
    List<ParentNotificationResponseDto> getParentNotifications(Long parentId);

    // teacher fetches their notifications
    List<TeacherNotificationResponseDto> getTeacherNotifications(Long teacherId);

    // mark as read
    void markAsRead(Long userNotificationId);
}