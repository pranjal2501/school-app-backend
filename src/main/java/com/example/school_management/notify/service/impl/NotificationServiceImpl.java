package com.example.school_management.notify.service.impl;

import com.example.school_management.notify.service.NotificationService;
import com.example.school_management.notify.dto.CreateNotificationRequestDto;
import com.example.school_management.notify.dto.NotificationResponseDto;
import com.example.school_management.notify.entity.Notification;
import com.example.school_management.notify.mapper.NotificationMapper;
import com.example.school_management.notify.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public NotificationResponseDto createNotification(CreateNotificationRequestDto request) {
        Notification notification = notificationMapper.toEntity(request);
        return notificationMapper.toResponseDto(notificationRepository.save(notification));
    }

    @Override
    public List<NotificationResponseDto> getAllNotifications() {
        return notificationRepository.findAll()
                .stream()
                .map(notificationMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}