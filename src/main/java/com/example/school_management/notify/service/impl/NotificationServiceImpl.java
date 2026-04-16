package com.example.school_management.notify.service.impl;

import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.enums.Role;
import com.example.school_management.notify.service.NotificationService;
import com.example.school_management.notify.dto.CreateNotificationRequestDto;
import com.example.school_management.notify.dto.NotificationResponseDto;
import com.example.school_management.notify.entity.Notification;
import com.example.school_management.notify.mapper.NotificationMapper;
import com.example.school_management.notify.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final RoleVerifier roleVerifier; // Injected for strict security checks

    private void verifyAdminAccess() {
        if (!roleVerifier.hasRole(Role.ADMIN)) {
            throw new AccessDeniedException("Access Denied: Only Admins can perform this action.");
        }
    }

    @Override
    public NotificationResponseDto createNotification(CreateNotificationRequestDto request) {
        verifyAdminAccess();

        Notification notification = notificationMapper.toEntity(request);
        return notificationMapper.toResponseDto(notificationRepository.save(notification));
    }

    @Override
    public List<NotificationResponseDto> getAllNotifications() {
        verifyAdminAccess();

        return notificationRepository.findAll()
                .stream()
                .map(notificationMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}