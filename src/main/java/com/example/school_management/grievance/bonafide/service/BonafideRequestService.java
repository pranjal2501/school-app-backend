package com.example.school_management.grievance.bonafide.service;

import com.example.school_management.common.enums.BonafideStatus;
import com.example.school_management.grievance.bonafide.dto.*;
import com.example.school_management.grievance.bonafide.entity.BonafideRequest;
import com.example.school_management.grievance.bonafide.entity.BonafideTemplate;
import com.example.school_management.grievance.bonafide.mapper.BonafideMapper;
import com.example.school_management.grievance.bonafide.repository.BonafideRequestRepository;
import com.example.school_management.mail.service.EmailService;
import com.example.school_management.parent.entity.Parent;
import com.example.school_management.parent.repository.ParentRepository;
import com.example.school_management.student.entity.Student;
import com.example.school_management.student.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BonafideRequestService {

    private final BonafideRequestRepository requestRepository;
    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final BonafideTemplateService templateService;
    private final BonafideImageService imageService;
    private final EmailService emailService;
    private final BonafideMapper bonafideMapper;

    // ── PARENT ACTIONS ─────────────────────────────────────────────────────────

    @Transactional
    public BonafideRequestResponse submitRequest(Long parentId, BonafideRequestCreate dto) {

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
    public List<BonafideRequestResponse> getMyRequests(Long parentId) {
        return requestRepository.findByRequestedById(parentId)
                .stream()
                .map(bonafideMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ── ADMIN ACTIONS ──────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<BonafideRequestResponse> getAllPendingRequests() {
        return requestRepository.findByStatus(BonafideStatus.PENDING)
                .stream()
                .map(bonafideMapper::toResponse)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<BonafideRequestResponse> getAllRequests() {
        return requestRepository.findAll()
                .stream()
                .map(bonafideMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BonafideRequestResponse approveRequest(Long requestId) {

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

        return bonafideMapper.toResponse(requestRepository.save(request));
    }

    @Transactional
    public BonafideRequestResponse rejectRequest(Long requestId, BonafideRejectRequest dto) {

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
}
