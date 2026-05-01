package com.example.school_management.result.service;

import com.example.school_management.admin.entity.Admin;
import com.example.school_management.admin.repository.AdminRepository;
import com.example.school_management.common.ApiResponse;
import com.example.school_management.common.enums.notify.AudienceType;
import com.example.school_management.common.enums.notify.NotificationType;
import com.example.school_management.common.exception.ResourceNotFoundException;
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
import java.util.List;
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
    private final ResultMapper resultMapper;

    public ResponseEntity<ApiResponse<LetterHeadResponseDto>> saveLetterhead(LetterheadRequestDto requestDto) {

        ResultLetterhead isPresent= resultLetterHeadRepository.findByExamTermAndAcademicYear(requestDto.getExamTerm(), requestDto.getAcademicYear()).orElse(null);

        if(isPresent != null) {
            isPresent.setUrl(requestDto.getUrl());
            resultLetterHeadRepository.save(isPresent);
            return ResponseEntity.ok(ApiResponse.ok("Existing updated successfully",resultMapper.toLetterheadResponse(isPresent)));
        }

        ResultLetterhead letterhead = ResultLetterhead.builder()
                .url(requestDto.getUrl())
                .examTerm(requestDto.getExamTerm())
                .academicYear(requestDto.getAcademicYear())
                .build();
        resultLetterHeadRepository.save(letterhead);

        return ResponseEntity.ok(ApiResponse.ok("New Letter Head added successfully",resultMapper.toLetterheadResponse(isPresent)));
    }

    public void toggleResultPublish(ToggleResultStatus resultStatus) {


        ResultLetterhead letterhead = resultLetterHeadRepository
                .findByExamTermAndAcademicYear(resultStatus.getExamTerm(), resultStatus.getAcademicYear())
                .orElseThrow(() -> new ResourceNotFoundException(
                     "No result config found for examTerm: " + resultStatus.getExamTerm() + " and year: " + resultStatus.getAcademicYear()));

        letterhead.setIsResultPublished(resultStatus.getPublish());
        letterhead.setPublishedAt(resultStatus.getPublish() ? LocalDateTime.now() : null);
        resultLetterHeadRepository.save(letterhead);

        //================ fire notifications to parents and admin ===============
        if(resultStatus.getPublish().equals(Boolean.TRUE)) {
            List<Parent> parents = parentRepository.findAll();

            Notification notification = Notification.builder()
                    .title("Result")
                    .description("Attention parent, result is published on " + LocalDate.now() + " at " + LocalTime.now())
                    .shortInfo("Result Published")
                    .audienceType(AudienceType.PARENT)
                    .notificationType(NotificationType.RESULT)
                    .eventDateTime(LocalDateTime.now()) // immediate event
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

        String message= "Result Status : "+ (((resultStatus.getPublish()==true)?"Published":"Closed"))+" on " + LocalDate.now() + " at " + LocalTime.now();
        List<Admin> admin= adminRepository.findAll();

        Notification notification = Notification.builder()
                .title("Result")
                .description(message)
                .shortInfo("Result Status Changed")
                .audienceType(AudienceType.ADMIN)
                .notificationType(NotificationType.RESULT)
                .eventDateTime(LocalDateTime.now()) // immediate event
                .scheduledDispatchTime(LocalDateTime.now())
                .dispatched(true)
                .build();

        notificationRepository.save(notification);

        for(Admin ad : admin) {
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
                .findByExamTermAndAcademicYear(requestDto.getExamTerm(), requestDto.getAcademicYear())
                .orElseThrow(() -> new ResourceNotFoundException("Result not configured for this exam term"));

        if (!Boolean.TRUE.equals(letterhead.getIsResultPublished())) {
            throw new IllegalStateException("Result has not been published yet by the administration");
        }


        // 1. fetch student
        Student student = studentRepository.findByRegId(requestDto.getRegistrationId()).orElseThrow(()-> new ResourceNotFoundException("Student","Registration Id ", requestDto.getRegistrationId()));

        // 2. fetch marks
        List<StudentSubjectMark> marks = markRepository
                .findByStudentAndExamTerm(student, requestDto.getExamTerm());

        if (marks.isEmpty()) {
            throw new ResourceNotFoundException("No marks found for student: "
                    + requestDto.getRegistrationId());
        }

        // 3. map to dtos
        List<SubjectMarkDto> subjectMarkDtos = marks.stream()
                .map(resultMapper::toSubjectMarkDto)
                .collect(Collectors.toList());

        // 4. build response
        StudentResultResponseDto response = resultMapper.toResultResponseDto(
                student,
                marks,
                subjectMarkDtos,
                requestDto.getExamTerm(),
                requestDto.getAcademicYear(),
                letterhead);

        // 5. generate PDF and email to parent
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
