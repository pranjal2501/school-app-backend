package com.example.school_management.grievance.bonafide.service;

import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.enums.BonafideStatus;
import com.example.school_management.common.enums.Role;
import com.example.school_management.common.enums.notify.AudienceType;
import com.example.school_management.common.enums.notify.NotificationType;
import com.example.school_management.grievance.bonafide.dto.*;
import com.example.school_management.grievance.bonafide.entity.BonafideRequest;
import com.example.school_management.grievance.bonafide.entity.BonafideTemplate;
import com.example.school_management.grievance.bonafide.mapper.BonafideMapper;
import com.example.school_management.grievance.bonafide.repository.BonafideRequestRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BonafideRequestService {

    private final BonafideRequestRepository requestRepository;
    private final StudentRepository studentRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final NotificationRepository notificationRepository;
    private final ParentRepository parentRepository;
    private final BonafideTemplateService templateService;
    private final BonafideImageService imageService;
    private final EmailService emailService;
    private final BonafideMapper bonafideMapper;
    private final RoleVerifier roleVerifier;

    // ── PARENT ACTIONS ─────────────────────────────────────────────────────────

    @Transactional
    public BonafideRequestResponse submitRequest(Long parentId, BonafideRequestCreate dto) throws AccessDeniedException {
        if(!roleVerifier.hasRole(Role.PARENT))
        {
            throw new AccessDeniedException("Only admin can access");
        }

        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new EntityNotFoundException("Parent not found"));

        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        if (!student.getParent().getId().equals(parentId)) {
            throw new SecurityException("You are not authorized to request bonafide for this student");
        }

        if (requestRepository.existsByStudentIdAndStatus(student.getId(), BonafideStatus.PENDING)) {
            throw new IllegalStateException(
                    "A bonafide request for this student is already pending approval");
        }

        BonafideRequest request = BonafideRequest.builder()
                .student(student)
                .requestedBy(parent)
                .purpose(dto.getPurpose())
                .status(BonafideStatus.PENDING)
                .build();

        return bonafideMapper.toResponse(requestRepository.save(request));
    }

    @Transactional(readOnly = true)
    public List<BonafideRequestResponse> getMyRequests(Long parentId) throws AccessDeniedException {

        if(!roleVerifier.hasRole(Role.PARENT))
        {
            throw new AccessDeniedException("Only admin can access");
        }
        return requestRepository.findByRequestedById(parentId)
                .stream()
                .map(bonafideMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ── ADMIN ACTIONS ──────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<BonafideRequestResponse> getAllPendingRequests() throws AccessDeniedException {
        if(!roleVerifier.hasRole(Role.ADMIN))
        {
            throw new AccessDeniedException("Only admin can access");
        }
        return requestRepository.findByStatus(BonafideStatus.PENDING)
                .stream()
                .map(bonafideMapper::toResponse)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<BonafideRequestResponse> getAllRequests() throws AccessDeniedException {

        if(!roleVerifier.hasRole(Role.ADMIN))
        {
            throw new AccessDeniedException("Only admin can access");
        }

        return requestRepository.findAll()
                .stream()
                .map(bonafideMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BonafideRequestResponse approveRequest(Long requestId) throws AccessDeniedException {

        if(!roleVerifier.hasRole(Role.ADMIN))
        {
            throw new AccessDeniedException("Only admin can access");
        }

        BonafideRequest request = getPendingRequestOrThrow(requestId);
        BonafideTemplate template = templateService.getActiveTemplateEntity();

        Student student = request.getStudent();
        validateStudentData(student);
        Parent parent = request.getRequestedBy();

        byte[] imageBytes = imageService.generate(template, student);

        String studentName = student.getFirstName() + " " + student.getLastName();
        String parentName = parent.getFatherFirstName() + " " + parent.getFatherLastName();
        emailService.sendBonafideApproved(parent.getEmail(), parentName, studentName, imageBytes);

        request.setStatus(BonafideStatus.APPROVED);
        request.setResolvedAt(LocalDateTime.now());

        BonafideRequest saved = requestRepository.save(request);
        requestRepository.flush();

        notifyParentsOnApprove(student);
        return bonafideMapper.toResponse(saved);
    }

    @Transactional
    public BonafideRequestResponse rejectRequest(Long requestId, BonafideRejectRequest dto) throws AccessDeniedException {

        if(!roleVerifier.hasRole(Role.ADMIN))
        {
            throw new AccessDeniedException("Only admin can access");
        }
        BonafideRequest request = getPendingRequestOrThrow(requestId);

        request.setStatus(BonafideStatus.REJECTED);
        request.setRemarks(dto.getRemarks());
        request.setResolvedAt(LocalDateTime.now());

        String studentName = request.getStudent().getFirstName() + " " + request.getStudent().getLastName();
        String parentName  = request.getRequestedBy().getFatherFirstName()  + " " + request.getRequestedBy().getFatherLastName();
        emailService.sendBonafideRejected(request.getRequestedBy().getEmail(), parentName, studentName, dto.getRemarks());

        return bonafideMapper.toResponse(requestRepository.save(request));
    }

    // ── private helpers ────────────────────────────────────────────────────────

    private BonafideRequest getPendingRequestOrThrow(Long requestId) {
        BonafideRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Bonafide request not found with id: " + requestId));

        if (request.getStatus() != BonafideStatus.PENDING) {
            throw new IllegalStateException(
                    "Request is already " + request.getStatus().name().toLowerCase()
                            + " and cannot be modified");
        }
        return request;
    }

    private void validateStudentData(Student student) {
        List<String> missing = new ArrayList<>();
        if (student.getCategory() == null) missing.add("category");
        if (student.getReligion() == null) missing.add("religion");
        if (student.getDob() == null) missing.add("date of birth");
        if (student.getNationality() == null) missing.add("nationality");

        if (!missing.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot generate bonafide — student profile is incomplete. Missing: "
                            + String.join(", ", missing));
        }
    }

    private void notifyParentsOnApprove(Student student) {
        Parent parent=student.getParent();
        if (parent == null) return;

        // 1. Create Notification
        Notification notification = Notification.builder()
                .title("Bonafide request approved")
                .description("Attention :  ")
                .shortInfo("We are pleased to inform you that the bonafide certificate request for your child ,"+ student.getFirstName() + " has been successfully approved.")
                .audienceType(AudienceType.PARENT)
                .notificationType(NotificationType.UPDATE)
                .eventDateTime(LocalDateTime.now()) // immediate event
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
