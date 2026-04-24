package com.example.school_management.result.service;

import com.example.school_management.academics.entity.ClassSubjectTeacher;
import com.example.school_management.academics.repository.ClassSubjectTeacherRepository;
import com.example.school_management.common.enums.ExamTerm;
import com.example.school_management.common.enums.MarkFillingStatus;
import com.example.school_management.common.enums.notify.AudienceType;
import com.example.school_management.common.enums.notify.NotificationType;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.notify.entity.Notification;
import com.example.school_management.notify.entity.UserNotification;
import com.example.school_management.notify.repository.NotificationRepository;
import com.example.school_management.notify.repository.UserNotificationRepository;
import com.example.school_management.result.dto.adminView.MarkFillingStatusResponseDto;
import com.example.school_management.result.repository.StudentSubjectMarkRepository;
import com.example.school_management.student.repository.StudentRepository;
import com.example.school_management.teacher.entity.Teacher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminResultService {

    private final ClassSubjectTeacherRepository classSubjectTeacherRepository;
    private final StudentRepository studentRepository;
    private final StudentSubjectMarkRepository studentSubjectMarkRepository;
    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;

    public List<MarkFillingStatusResponseDto> getMarkFillingStatus(Long classId, ExamTerm examTerm) {
        List<ClassSubjectTeacher> allocations = classSubjectTeacherRepository.findBySchoolClassId(classId);

        if (allocations.isEmpty()) {
            throw new ResourceNotFoundException("Class", "id", classId);
        }

        return allocations.stream()
                .map(allocation -> {
                    int totalStudents = studentRepository.countByAssignedClassId(
                            allocation.getSchoolClass().getId());
                    int filledCount = studentSubjectMarkRepository.countByAllocationIdAndExamTerm(
                            allocation.getId(), examTerm);

                    return MarkFillingStatusResponseDto.builder()
                            .allocationId(allocation.getId())
                            .className(allocation.getSchoolClass().getClassName())
                            .subjectName(allocation.getSubject().getSubjectName())
                            .teacherName(allocation.getTeacher().getFirstName()
                                    + " " + allocation.getTeacher().getLastName())
                            .examTerm(examTerm)
                            .status(allocation.getMarkFillingStatus())
                            .totalStudents(totalStudents)
                            .marksFilledCount(filledCount)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public void notifyPendingTeachers(Long classId, ExamTerm examTerm) {

        List<ClassSubjectTeacher> allocations = classSubjectTeacherRepository
                .findBySchoolClassId(classId);

        if (allocations.isEmpty()) {
            throw new ResourceNotFoundException("Class", "id", classId);
        }

        List<ClassSubjectTeacher> pendingAllocations = allocations.stream()
                .filter(a -> a.getMarkFillingStatus() != MarkFillingStatus.COMPLETE)
                .collect(Collectors.toList());

        if (pendingAllocations.isEmpty()) {
            throw new IllegalStateException("All teachers have filled marks for this class.");
        }

        for (ClassSubjectTeacher allocation : pendingAllocations) {
            Teacher teacher = allocation.getTeacher();

            Notification notification = Notification.builder()
                    .title("Marks Filling Reminder")
                    .description("You have not filled marks for "
                            + allocation.getSubject().getSubjectName()
                            + " - " + allocation.getSchoolClass().getClassName()
                            + " for " + examTerm.name() + " exam term.")
                    .shortInfo("Please fill marks for "
                            + allocation.getSubject().getSubjectName())
                    .audienceType(AudienceType.TEACHER)
                    .notificationType(NotificationType.REMINDER)
                    .eventDateTime(LocalDateTime.now())
                    .scheduledDispatchTime(LocalDateTime.now())
                    .dispatched(true)
                    .teacherIds(Set.of(teacher.getId()))
                    .build();

            notificationRepository.saveAndFlush(notification);

            UserNotification userNotification = UserNotification.builder()
                    .notification(notification)
                    .teacher(teacher)
                    .isRead(false)
                    .deliveredAt(LocalDateTime.now())
                    .build();

            userNotificationRepository.save(userNotification);
        }
    }
}
