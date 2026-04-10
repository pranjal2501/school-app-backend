package com.example.school_management.notify.service;

import com.example.school_management.notify.dto.CreateNotificationRequestDto;
import com.example.school_management.notify.dto.NotificationResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

public interface NotificationService {
    NotificationResponseDto createNotification(CreateNotificationRequestDto request);
    List<NotificationResponseDto> getAllNotifications();
}