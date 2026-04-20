package com.example.school_management.complaint.service;

import com.example.school_management.admin.entity.Admin;
import com.example.school_management.admin.repository.AdminRepository;
import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.enums.ComplaintStatus;
import com.example.school_management.common.enums.Role;
import com.example.school_management.common.enums.notify.AudienceType;
import com.example.school_management.common.enums.notify.NotificationType;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.common.exception.UnauthorizedAccessException;
import com.example.school_management.complaint.dto.*;
import com.example.school_management.complaint.entity.Complaint;
import com.example.school_management.complaint.mappper.ComplaintMapper;
import com.example.school_management.complaint.repository.ComplaintRepository;
import com.example.school_management.employee.entity.Employee;
import com.example.school_management.employee.repository.EmployeeRepository;
import com.example.school_management.notify.dto.CreateNotificationRequestDto;
import com.example.school_management.notify.entity.Notification;
import com.example.school_management.notify.entity.UserNotification;
import com.example.school_management.notify.repository.NotificationRepository;
import com.example.school_management.notify.repository.UserNotificationRepository;
import com.example.school_management.notify.service.impl.NotificationServiceImpl;
import com.example.school_management.parent.entity.Parent;
import com.example.school_management.student.entity.Student;
import com.example.school_management.student.repository.StudentRepository;
import com.example.school_management.teacher.entity.Teacher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final StudentRepository studentRepository;
    private final EmployeeRepository employeeRepository;
    private final ComplaintMapper complaintMapper;
    private final AdminRepository adminRepository;
    private final RoleVerifier roleVerifier; // Injected for strict security context checks
    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ComplaintResponseDto raiseComplaint(ComplaintRequestDto request) {

        // Securely retrieve the current session details
        String currentUser = roleVerifier.getCurrentUsername();
        boolean isAdmin = roleVerifier.hasRole(Role.ADMIN);

        // 1. Strict Validation: Target student
        Student targetStudent = studentRepository.findById(request.getTargetStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getTargetStudentId()));

        // 2. Mapping
        Complaint complaint = complaintMapper.toEntity(request);
        complaint.setTargetStudent(targetStudent);

        // 3. Smart Raiser Resolution based on Role
        if (isAdmin) {
            Admin adminRaiser = adminRepository.findByUsername(currentUser)
                    .orElseThrow(() -> new UnauthorizedAccessException("Admin session is invalid."));
            complaint.setReportedByAdmin(adminRaiser);
        } else {
            Employee empRaiser = employeeRepository.findByIdentifier(currentUser)
                    .orElseThrow(() -> new UnauthorizedAccessException("Teacher session is invalid."));
            complaint.setReportedByEmployee(empRaiser);
        }

        Complaint saved=complaintRepository.save(complaint);
        notifyParentsOnComplaint(complaint, targetStudent);
        // 4. Save and Return
        return complaintMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public ComplaintResponseDto updateComplaintStatus(String ticketNumber, ComplaintStatusUpdateDto request) {

        // Securely retrieve the current session details
        String currentUser = roleVerifier.getCurrentUsername();
        boolean isAdmin = roleVerifier.hasRole(Role.ADMIN);

        Complaint complaint = complaintRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", "ticketNumber", ticketNumber));

        // --- 1. STRICT DOMAIN AUTHORIZATION: Admins or Class Teacher Only ---
        if (!isAdmin) {
            if (!isClassTeacher(currentUser, complaint)) {
                throw new UnauthorizedAccessException("Access Denied: Only an Administrator or the student's Class Teacher can resolve this complaint.");
            }
        }

        // --- 2. STATE MACHINE LOGIC ---
        if (complaint.getStatus() == ComplaintStatus.RESOLVED) {
            throw new IllegalStateException("This complaint is already resolved and cannot be modified.");
        }

        if (request.getNewStatus() == ComplaintStatus.RESOLVED && request.getResolutionComment() == null) {
            throw new IllegalArgumentException("A resolution comment is strictly required when resolving a complaint.");
        }

        // --- 3. FETCH DEPENDENCIES FIRST (Prevents Auto-Flush Crashes) ---
        Admin adminResolver = null;
        Employee employeeResolver = null;

        if (request.getNewStatus() == ComplaintStatus.RESOLVED) {
            if (isAdmin) {
                adminResolver = adminRepository.findByUsername(currentUser)
                        .orElseThrow(() -> new UnauthorizedAccessException("Admin identity could not be verified."));
            } else {
                employeeResolver = employeeRepository.findByIdentifier(currentUser)
                        .orElseThrow(() -> new UnauthorizedAccessException("Teacher identity could not be verified."));
            }
        }

        // --- 4. APPLY ALL CHANGES TOGETHER ---
        complaint.setStatus(request.getNewStatus());

        if (request.getResolutionComment() != null) {
            complaint.setResolutionComment(request.getResolutionComment());
        }

        if (adminResolver != null) {
            complaint.setResolvedByAdmin(adminResolver);
        } else if (employeeResolver != null) {
            complaint.setResolvedBy(employeeResolver);
        }

        // --- 5. SAVE AND MAP ---
        return complaintMapper.toResponseDto(complaintRepository.save(complaint));
    }

    private static boolean isClassTeacher(String currentUser, Complaint complaint) {
        // Defensively navigate the relationships to find the Class Teacher
        if (complaint.getTargetStudent() != null && complaint.getTargetStudent().getAssignedClass() != null) {
            Teacher classTeacher = complaint.getTargetStudent().getAssignedClass().getClassTeacher();

            // Check if the current user is the class teacher
            if (classTeacher != null && classTeacher.isIdentifiedBy(currentUser)) {
                return true;
            }
        }
        return false;
    }
//   ===============================Helper methods=========================

    private void notifyParentsOnComplaint(Complaint complaint, Student targetStudent) {
        Parent parent=targetStudent.getParent();
        if (parent == null) return;

        // 1. Create Notification
        Notification notification = Notification.builder()
                .title(complaint.getTitle())
                .description("Complaint : " +complaint.getDescription())
                .shortInfo("Complaint raised for your child"+complaint.getTargetStudent().getFirstName())
                .audienceType(AudienceType.PARENT)
                .notificationType(NotificationType.COMPLAINT)
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