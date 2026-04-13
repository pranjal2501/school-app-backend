package com.example.school_management.notify.service.impl;


import com.example.school_management.common.enums.notify.AudienceType;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.notify.entity.Notification;
import com.example.school_management.notify.entity.UserNotification;
import com.example.school_management.notify.mapper.UserNotificationMapper;
import com.example.school_management.notify.repository.NotificationRepository;
import com.example.school_management.notify.repository.UserNotificationRepository;
import com.example.school_management.notify.service.UserNotificationService;
import com.example.school_management.parent.entity.Parent;
import com.example.school_management.parent.repository.ParentRepository;
import com.example.school_management.teacher.entity.Teacher;
import com.example.school_management.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.example.school_management.notify.dto.ParentNotificationResponseDto;
import com.example.school_management.notify.dto.TeacherNotificationResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserNotificationServiceImpl implements UserNotificationService {

    private final UserNotificationRepository userNotificationRepository;
    private final NotificationRepository notificationRepository;
    private final ParentRepository parentRepository;
    private final TeacherRepository teacherRepository;
    private final UserNotificationMapper userNotificationMapper;

    @Scheduled(fixedDelay = 60000) // runs every minute
    @Override
    public void dispatchNotifications() {
        List<Notification> pending = notificationRepository
                .findByDispatchedFalseAndScheduledDispatchTimeBefore(LocalDateTime.now());

        for (Notification notification : pending) {
            if (notification.getAudienceType() == AudienceType.PARENT) {
                List<Parent> parents = parentRepository.findAll();
                for (Parent parent : parents) {
                    UserNotification un = UserNotification.builder()
                            .notification(notification)
                            .parent(parent)
                            .isRead(false)
                            .deliveredAt(LocalDateTime.now())
                            .build();
                    userNotificationRepository.save(un);
                }
            } else if (notification.getAudienceType() == AudienceType.TEACHER) {
                List<Teacher> teachers = teacherRepository.findAll();
                for (Teacher teacher : teachers) {
                    UserNotification un = UserNotification.builder()
                            .notification(notification)
                            .teacher(teacher)
                            .isRead(false)
                            .deliveredAt(LocalDateTime.now())
                            .build();
                    userNotificationRepository.save(un);
                }
            }
            notification.setDispatched(true);
            notificationRepository.save(notification);
        }
    }

    @Override
    public List<ParentNotificationResponseDto> getParentNotifications(Long parentId) {
        return userNotificationRepository.findByParentId(parentId)
                .stream()
                .map(userNotificationMapper::toParentResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TeacherNotificationResponseDto> getTeacherNotifications(Long teacherId) {
        return userNotificationRepository.findByTeacherId(teacherId)
                .stream()
                .map(userNotificationMapper::toTeacherResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(Long userNotificationId) {
        UserNotification un = userNotificationRepository.findById(userNotificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        un.setIsRead(true);
        un.setReadAt(LocalDateTime.now());
        userNotificationRepository.save(un);
    }
}