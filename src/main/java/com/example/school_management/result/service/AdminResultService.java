package com.example.school_management.result.service;

import com.example.school_management.academics.entity.ClassSubjectTeacher;
import com.example.school_management.academics.repository.ClassSubjectTeacherRepository;
import com.example.school_management.common.enums.MarkFillingStatus;
import com.example.school_management.common.enums.notify.AudienceType;
import com.example.school_management.common.enums.notify.NotificationType;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.exam.entity.ExamType;
import com.example.school_management.exam.repository.ExamTypeRepository;
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
    private final ExamTypeRepository examTypeRepository;

    public List<MarkFillingStatusResponseDto> getMarkFillingStatus(Long classId, Long examTypeId) {
        List<ClassSubjectTeacher> allocations = classSubjectTeacherRepository.findBySchoolClassId(classId);

        if (allocations.isEmpty()) {
            throw new ResourceNotFoundException("Class", "id", classId);
        }

        ExamType examType = examTypeRepository.findById(examTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("ExamType", "id", examTypeId));

        return allocations.stream()
                .filter(allocation -> !allocation.getSubject().isAggregateOnly())
                .map(allocation -> {
                    int totalStudents = studentRepository.countByAssignedClassId(allocation.getSchoolClass().getId());

                    int filledCount = studentSubjectMarkRepository.countDistinctStudentsByAllocationIdAndExamTypeId(
                            allocation.getId(), examTypeId);

                    MarkFillingStatus status = (filledCount == 0) ? MarkFillingStatus.NOT_FILLED
                            : (filledCount < totalStudents) ? MarkFillingStatus.PARTIAL
                            : MarkFillingStatus.COMPLETE;

                    return MarkFillingStatusResponseDto.builder()
                            .allocationId(allocation.getId())
                            .className(allocation.getSchoolClass().getClassName())
                            .subjectName(allocation.getSubject().getSubjectName())
                            .teacherName(allocation.getTeacher().getFirstName() + " " + allocation.getTeacher().getLastName())
                            .examTypeId(examType.getId())
                            .examTypeName(examType.getName())
                            .status(status)
                            .totalStudents(totalStudents)
                            .studentsGradedCount(filledCount)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public void notifyPendingTeachers(Long classId, Long examTypeId) {
        ExamType examType = examTypeRepository.findById(examTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("ExamType", "id", examTypeId));

        List<MarkFillingStatusResponseDto> statuses = getMarkFillingStatus(classId, examTypeId);

        List<MarkFillingStatusResponseDto> pendingAllocations = statuses.stream()
                .filter(s -> s.getStatus() != MarkFillingStatus.COMPLETE)
                .collect(Collectors.toList());

        if (pendingAllocations.isEmpty()) {
            throw new IllegalStateException("All teachers have completely filled marks for this class and exam.");
        }

        for (MarkFillingStatusResponseDto dto : pendingAllocations) {
            ClassSubjectTeacher allocation = classSubjectTeacherRepository.findById(dto.getAllocationId()).orElseThrow();
            Teacher teacher = allocation.getTeacher();

            Notification notification = Notification.builder()
                    .title("Marks Filling Reminder")
                    .description("You have pending marks to fill for "
                            + dto.getSubjectName() + " - " + dto.getClassName()
                            + " for " + examType.getName() + ".")
                    .shortInfo("Please complete marks for " + dto.getSubjectName())
                    .audienceType(AudienceType.TEACHER)
                    .notificationType(NotificationType.REMINDER)
                    .eventDateTime(LocalDateTime.now())
                    .scheduledDispatchTime(LocalDateTime.now())
                    .dispatched(true)
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