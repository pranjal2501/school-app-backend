package com.example.school_management.notify.service.impl;


import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.enums.Role;
import com.example.school_management.common.enums.notify.AudienceType;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.common.exception.UnauthorizedAccessException;
import com.example.school_management.notify.dto.ParentNotificationResponseDto;
import com.example.school_management.notify.entity.Notification;
import com.example.school_management.notify.entity.UserNotification;
import com.example.school_management.notify.mapper.UserNotificationMapper;
import com.example.school_management.notify.repository.NotificationRepository;
import com.example.school_management.notify.repository.UserNotificationRepository;
import com.example.school_management.notify.service.UserNotificationService;
import com.example.school_management.parent.entity.Parent;
import com.example.school_management.parent.repository.ParentRepository;
import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.student.entity.Student;
import com.example.school_management.student.repository.StudentRepository;
import com.example.school_management.teacher.entity.Teacher;
import com.example.school_management.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.example.school_management.notify.dto.GeneralParentNotificationResponseDto;
import com.example.school_management.notify.dto.TeacherNotificationResponseDto;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserNotificationServiceImpl implements UserNotificationService {

    private final UserNotificationRepository userNotificationRepository;
    private final NotificationRepository notificationRepository;
    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final UserNotificationMapper userNotificationMapper;
    private final RoleVerifier roleVerifier;

    @Scheduled(fixedDelay = 60000) // runs every minute
    @Override
    @Transactional
    public void dispatchNotifications() {
        List<Notification> pending = notificationRepository.findPendingNotifications(LocalDateTime.now());

        for (Notification notification : pending) {

            if(notification.getAudienceType().equals(AudienceType.PARENT))
            {
                List<Parent> parents;

                if (notification.getTargetClasses().isEmpty()) {
                    parents = parentRepository.findAll();
                }
                else {

                    List<Long> classIds = notification.getTargetClasses()
                            .stream()
                            .map(SchoolClass::getId)
                            .collect(Collectors.toList());

                    parents = studentRepository.findByAssignedClassIdIn(classIds)
                            .stream()
                            .map(Student::getParent)
                            .filter(Objects::nonNull)
                            .distinct()
                            .collect(Collectors.toList());
                }
                for (Parent parent : parents) {
                    UserNotification un = UserNotification.builder()
                            .notification(notification)
                            .parent(parent)
                            .isRead(false)
                            .deliveredAt(LocalDateTime.now())
                            .build();
                    userNotificationRepository.save(un);
                }
            }
            else if (notification.getAudienceType() == AudienceType.TEACHER) {
                List<Teacher> teachers;

                if (notification.getTeacherIds() != null && !notification.getTeacherIds().isEmpty()) {
                    teachers = teacherRepository.findAllById(notification.getTeacherIds());
                } else {
                    teachers = teacherRepository.findAll();
                }
                List<UserNotification> batch = new ArrayList<>();
                for (Teacher teacher : teachers) {
                    batch.add(UserNotification.builder()
                            .notification(notification)
                            .teacher(teacher)
                            .isRead(false)
                            .deliveredAt(LocalDateTime.now())
                            .build());
                }
                userNotificationRepository.saveAll(batch);
            }
            notification.setDispatched(true);
            notificationRepository.save(notification);
        }
    }

    @Override
    public List<ParentNotificationResponseDto> getParentNotifications(Long parentId) {

        String currentUser = roleVerifier.getCurrentUsername();
        boolean isParent = roleVerifier.hasRole(Role.PARENT);

        if (!isParent) {
            Parent parent = parentRepository.findById(parentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", parentId));

            if (!parent.isIdentifiedBy(currentUser)) {
                throw new UnauthorizedAccessException("Access Denied: You can only view your own notifications.");
            }
        }

        return userNotificationRepository.findByParentId(parentId)
                .stream()
                .map(userNotificationMapper::toParentResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TeacherNotificationResponseDto> getTeacherNotifications(Long teacherId) {

        String currentUser = roleVerifier.getCurrentUsername();
        boolean isTeacher = roleVerifier.hasRole(Role.TEACHER);

        if (!isTeacher) {
            Teacher teacher = teacherRepository.findById(teacherId)
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", teacherId));

            if (!teacher.isIdentifiedBy(currentUser)) {
                throw new UnauthorizedAccessException("Access Denied: You can only view your own notifications.");
            }
        }

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