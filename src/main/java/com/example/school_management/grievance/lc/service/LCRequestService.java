package com.example.school_management.grievance.lc.service;

import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.enums.GrievanceStatus;
import com.example.school_management.common.enums.LetterheadType;
import com.example.school_management.common.enums.Role;
import com.example.school_management.common.enums.notify.AudienceType;
import com.example.school_management.common.enums.notify.NotificationType;
import com.example.school_management.grievance.template.dto.PlaceholderContext;
import com.example.school_management.grievance.template.util.PlaceholderContextBuilder;
import com.example.school_management.grievance.lc.dto.*;
import com.example.school_management.grievance.lc.entity.LCRequest;
import com.example.school_management.grievance.lc.mapper.LCMapper;
import com.example.school_management.grievance.lc.repository.LCRequestRepository;
import com.example.school_management.grievance.template.entity.LetterheadTemplate;
import com.example.school_management.grievance.template.service.LetterheadTemplateService;
import com.example.school_management.mail.service.EmailService;
import com.example.school_management.notify.entity.Notification;
import com.example.school_management.notify.entity.UserNotification;
import com.example.school_management.notify.repository.NotificationRepository;
import com.example.school_management.notify.repository.UserNotificationRepository;
import com.example.school_management.parent.entity.Parent;
import com.example.school_management.parent.repository.ParentRepository;
import com.example.school_management.student.entity.Student;
import com.example.school_management.student.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LCRequestService {

    private final LCRequestRepository requestRepository;
    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final LetterheadTemplateService templateService;
    private final LCImageService imageService;
    private final EmailService emailService;
    private final LCMapper lcMapper;
    private final RoleVerifier roleVerifier;
    private final PlaceholderContextBuilder contextBuilder;
    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;

    // ── PARENT ACTIONS ─────────────────────────────────────────────────────────

    @Transactional
    public LCRequestResponse submitRequest(Long parentId, LCCreateRequest dto) throws AccessDeniedException {
        if (!roleVerifier.hasRole(Role.PARENT)) {
            throw new AccessDeniedException("Only parents can submit LC requests");
        }

        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new EntityNotFoundException("Parent not found"));

        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        if (!student.getParent().getId().equals(parentId)) {
            throw new SecurityException("You are not authorized to request LC for this student");
        }

        if (requestRepository.existsByStudentIdAndStatus(student.getId(), GrievanceStatus.PENDING)) {
            throw new IllegalStateException("An LC request for this student is already pending approval");
        }

        LCRequest request = LCRequest.builder()
                .student(student)
                .requestedBy(parent)
                .reason(dto.getReason())
                .status(GrievanceStatus.PENDING)
                .build();

        return lcMapper.toResponse(requestRepository.save(request));
    }

    @Transactional(readOnly = true)
    public List<LCRequestResponse> getMyRequests(Long parentId) throws AccessDeniedException {
        if (!roleVerifier.hasRole(Role.PARENT)) {
            throw new AccessDeniedException("Only parents can view their requests");
        }
        return requestRepository.findByRequestedById(parentId)
                .stream()
                .map(lcMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ── ADMIN ACTIONS ──────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<LCRequestResponse> getAllRequests() throws AccessDeniedException {
        if (!roleVerifier.hasRole(Role.ADMIN)) {
            throw new AccessDeniedException("Only admin can access");
        }
        return requestRepository.findAll()
                .stream()
                .map(lcMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LCRequestResponse> getAllPendingRequests() throws AccessDeniedException {
        if (!roleVerifier.hasRole(Role.ADMIN)) {
            throw new AccessDeniedException("Only admin can access");
        }
        return requestRepository.findByStatus(GrievanceStatus.PENDING)
                .stream()
                .map(lcMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public LCRequestResponse approveRequest(Long requestId, LCRequestApprove dto) throws AccessDeniedException {
        if (!roleVerifier.hasRole(Role.ADMIN)) {
            throw new AccessDeniedException("Only admin can approve LC requests");
        }

        LCRequest request = getPendingRequestOrThrow(requestId);
        LetterheadTemplate template = templateService.getByTypeEntity(LetterheadType.LEAVING_CERTIFICATE);

        Student student = request.getStudent();
        Parent parent = request.getRequestedBy();

        // set admin filled fields
        request.setLeavingDate(dto.getLeavingDate());
        request.setConduct(dto.getConduct());
        request.setLastAttendedYear(dto.getLastAttendedYear());
        request.setStatus(GrievanceStatus.APPROVED);
        request.setResolvedAt(LocalDateTime.now());

        PlaceholderContext context = contextBuilder.forLC(
                student,
                dto.getLeavingDate().toString(),
                request.getReason(),
                dto.getConduct(),
                dto.getLastAttendedYear()
        );

        byte[] imageBytes = imageService.generate(template, context);

        String studentName = student.getFirstName() + " " + student.getLastName();
        String parentName = parent.getFatherFirstName() + " " + parent.getFatherLastName();
        emailService.sendLCApproved(parent.getEmail(), parentName, studentName, imageBytes);

        LCRequest saved = requestRepository.save(request);
        requestRepository.flush();

        notifyParentOnApprove(student);
        return lcMapper.toResponse(saved);
    }

    @Transactional
    public LCRequestResponse rejectRequest(Long requestId, LCRequestReject dto) throws AccessDeniedException {
        if (!roleVerifier.hasRole(Role.ADMIN)) {
            throw new AccessDeniedException("Only admin can reject LC requests");
        }

        LCRequest request = getPendingRequestOrThrow(requestId);

        request.setStatus(GrievanceStatus.REJECTED);
        request.setRemarks(dto.getRemarks());
        request.setResolvedAt(LocalDateTime.now());

        String studentName = request.getStudent().getFirstName() + " " + request.getStudent().getLastName();
        String parentName = request.getRequestedBy().getFatherFirstName() + " " + request.getRequestedBy().getFatherLastName();
        emailService.sendLCRejected(request.getRequestedBy().getEmail(), parentName, studentName, dto.getRemarks());

        return lcMapper.toResponse(requestRepository.save(request));
    }

    // ── Private Helpers ────────────────────────────────────────────────────────

    private LCRequest getPendingRequestOrThrow(Long requestId) {
        LCRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("LC request not found with id: " + requestId));

        if (request.getStatus() != GrievanceStatus.PENDING) {
            throw new IllegalStateException(
                    "Request is already " + request.getStatus().name().toLowerCase()
                            + " and cannot be modified");
        }
        return request;
    }

    private void notifyParentOnApprove(Student student) {
        Parent parent = student.getParent();
        if (parent == null) return;

        Notification notification = Notification.builder()
                .title("LC request approved")
                .description("Attention : ")
                .shortInfo("We are pleased to inform you that the leaving certificate request for your child, "
                        + student.getFirstName() + " has been successfully approved.")
                .audienceType(AudienceType.PARENT)
                .notificationType(NotificationType.UPDATE)
                .eventDateTime(LocalDateTime.now())
                .scheduledDispatchTime(LocalDateTime.now())
                .dispatched(true)
                .build();

        notificationRepository.save(notification);

        UserNotification userNotification = UserNotification.builder()
                .notification(notification)
                .parent(parent)
                .deliveredAt(LocalDateTime.now())
                .build();

        userNotificationRepository.save(userNotification);
    }
}