package com.example.school_management.result.service;

import com.example.school_management.academics.entity.ClassSubjectTeacher;
import com.example.school_management.admin.entity.Admin;
import com.example.school_management.admin.repository.AdminRepository;
import com.example.school_management.common.ApiResponse;
import com.example.school_management.common.enums.notify.AudienceType;
import com.example.school_management.common.enums.notify.NotificationType;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.exam.entity.ExamType;
import com.example.school_management.exam.entity.MarkingScheme;
import com.example.school_management.exam.repository.ExamTypeRepository;
import com.example.school_management.mail.service.EmailService;
import com.example.school_management.notify.entity.Notification;
import com.example.school_management.notify.entity.UserNotification;
import com.example.school_management.notify.repository.NotificationRepository;
import com.example.school_management.notify.repository.UserNotificationRepository;
import com.example.school_management.parent.entity.Parent;
import com.example.school_management.parent.repository.ParentRepository;
import com.example.school_management.result.dto.resultDisplay.*;
import com.example.school_management.result.entity.ResultLetterhead;
import com.example.school_management.result.entity.StudentSubjectMark;
import com.example.school_management.result.mapper.ResultMapper;
import com.example.school_management.result.repository.ResultLetterHeadRepository;
import com.example.school_management.result.repository.StudentSubjectMarkRepository;
import com.example.school_management.student.entity.Student;
import com.example.school_management.student.repository.StudentRepository;
import com.example.school_management.subject.entity.Subject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResultDisplayService {

    private final EmailService emailService;
    private final ResultPdfService resultPdfService;
    private final ResultLetterHeadRepository resultLetterHeadRepository;
    private final StudentRepository studentRepository;
    private final StudentSubjectMarkRepository markRepository;
    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final AdminRepository adminRepository;
    private final ParentRepository parentRepository;
    private final ExamTypeRepository examTypeRepository;
    private final ResultMapper resultMapper;

    public ResponseEntity<ApiResponse<LetterHeadResponseDto>> saveLetterhead(LetterheadRequestDto requestDto) {

        ResultLetterhead isPresent = resultLetterHeadRepository
                .findByExamTypeIdAndAcademicYear(requestDto.getExamTypeId(), requestDto.getAcademicYear())
                .orElse(null);

        if(isPresent != null) {
            isPresent.setUrl(requestDto.getUrl());
            resultLetterHeadRepository.save(isPresent);
            return ResponseEntity.ok(ApiResponse.ok("Existing updated successfully", resultMapper.toLetterheadResponse(isPresent)));
        }

        ResultLetterhead letterhead = ResultLetterhead.builder()
                .url(requestDto.getUrl())
                .examTypeId(requestDto.getExamTypeId())
                .academicYear(requestDto.getAcademicYear())
                .build();
        resultLetterHeadRepository.save(letterhead);

        return ResponseEntity.ok(ApiResponse.ok("New Letter Head added successfully", resultMapper.toLetterheadResponse(letterhead)));
    }

    public void toggleResultPublish(ToggleResultStatus resultStatus) {

        ResultLetterhead letterhead = resultLetterHeadRepository
                .findByExamTypeIdAndAcademicYear(resultStatus.getExamTypeId(), resultStatus.getAcademicYear())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No result config found for examTypeId: " + resultStatus.getExamTypeId() + " and year: " + resultStatus.getAcademicYear()));

        letterhead.setIsResultPublished(resultStatus.getPublish());
        letterhead.setPublishedAt(resultStatus.getPublish() ? LocalDateTime.now() : null);
        resultLetterHeadRepository.save(letterhead);

        if(Boolean.TRUE.equals(resultStatus.getPublish())) {
            List<Parent> parents = parentRepository.findAll();

            Notification notification = Notification.builder()
                    .title("Result Published")
                    .description("Attention parent, results have been published on " + LocalDate.now() + " at " + LocalTime.now())
                    .shortInfo("Result Published")
                    .audienceType(AudienceType.PARENT)
                    .notificationType(NotificationType.RESULT)
                    .eventDateTime(LocalDateTime.now())
                    .scheduledDispatchTime(LocalDateTime.now())
                    .dispatched(true)
                    .build();

            notificationRepository.save(notification);

            for (Parent parent : parents) {
                UserNotification userNotification = UserNotification.builder()
                        .notification(notification)
                        .parent(parent)
                        .deliveredAt(LocalDateTime.now())
                        .build();

                userNotificationRepository.save(userNotification);
            }
        }

        String message = "Result Status: " + (Boolean.TRUE.equals(resultStatus.getPublish()) ? "Published" : "Closed") + " on " + LocalDate.now() + " at " + LocalTime.now();
        List<Admin> admins = adminRepository.findAll();

        Notification notification = Notification.builder()
                .title("Result Status Update")
                .description(message)
                .shortInfo("Result Status Changed")
                .audienceType(AudienceType.ADMIN)
                .notificationType(NotificationType.RESULT)
                .eventDateTime(LocalDateTime.now())
                .scheduledDispatchTime(LocalDateTime.now())
                .dispatched(true)
                .build();

        notificationRepository.save(notification);

        for(Admin ad : admins) {
            UserNotification userNotification = UserNotification.builder()
                    .notification(notification)
                    .admin(ad)
                    .deliveredAt(LocalDateTime.now())
                    .build();

            userNotificationRepository.save(userNotification);
        }
    }

    public StudentResultResponseDto getStudentResult(StudentResultRequestDto requestDto) {

        ResultLetterhead letterhead = resultLetterHeadRepository
                .findByExamTypeIdAndAcademicYear(requestDto.getExamTypeId(), requestDto.getAcademicYear())
                .orElseThrow(() -> new ResourceNotFoundException("Result not configured for this exam type"));

        if (!Boolean.TRUE.equals(letterhead.getIsResultPublished())) {
            throw new IllegalStateException("Result has not been published yet by the administration");
        }

        Student student = studentRepository.findByRegId(requestDto.getRegistrationId())
                .orElseThrow(()-> new ResourceNotFoundException("Student","Registration Id", requestDto.getRegistrationId()));

        ExamType examType = examTypeRepository.findById(requestDto.getExamTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("ExamType", "id", requestDto.getExamTypeId()));

        // Fetch all marks for this student for this specific exam
        List<StudentSubjectMark> allComponentMarks = markRepository
                .findAllByStudentIdAndExamTypeId(student.getId(), requestDto.getExamTypeId());

        if (allComponentMarks.isEmpty()) {
            throw new ResourceNotFoundException("No marks found for student: " + requestDto.getRegistrationId());
        }

        // Group component marks by their Subject Allocation
        Map<ClassSubjectTeacher, List<StudentSubjectMark>> marksByAllocation = allComponentMarks.stream()
                .collect(Collectors.groupingBy(StudentSubjectMark::getAllocation));

        List<SubjectMarkDto> subjectMarkDtos = new ArrayList<>();

        for (Map.Entry<ClassSubjectTeacher, List<StudentSubjectMark>> entry : marksByAllocation.entrySet()) {
            Subject subject = entry.getKey().getSubject();

            MarkingScheme scheme = subject.getMarkingSchemes().stream()
                    .filter(s -> s.getExamType().getId().equals(requestDto.getExamTypeId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Marking scheme missing for subject " + subject.getSubjectName()));

            subjectMarkDtos.add(resultMapper.toSubjectMarkDto(subject, scheme, entry.getValue()));
        }

        StudentResultResponseDto response = resultMapper.toResultResponseDto(
                student,
                subjectMarkDtos,
                examType.getName(),
                requestDto.getAcademicYear(),
                letterhead);

        try {
            byte[] pdf = resultPdfService.generateResultPdf(response);
            if (student.getParent() != null && student.getParent().getEmail() != null) {
                emailService.sendResultEmail(
                        student.getParent().getEmail(),
                        student.getFirstName() + " " + student.getLastName(),
                        pdf
                );
            }
        } catch (Exception e) {
            log.warn("Failed to generate/send result PDF for student: {}. Error: {}",
                    requestDto.getRegistrationId(), e.getMessage());
        }

        return response;
    }
}