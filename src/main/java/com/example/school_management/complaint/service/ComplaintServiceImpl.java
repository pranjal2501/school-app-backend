package com.example.school_management.complaint.service;

import com.example.school_management.admin.entity.Admin;
import com.example.school_management.admin.repository.AdminRepository;
import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.enums.ComplaintStatus;
import com.example.school_management.common.enums.Role;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.common.exception.UnauthorizedAccessException;
import com.example.school_management.complaint.dto.*;
import com.example.school_management.complaint.entity.Complaint;
import com.example.school_management.complaint.mappper.ComplaintMapper;
import com.example.school_management.complaint.repository.ComplaintRepository;
import com.example.school_management.employee.entity.Employee;
import com.example.school_management.employee.repository.EmployeeRepository;
import com.example.school_management.student.entity.Student;
import com.example.school_management.student.repository.StudentRepository;
import com.example.school_management.teacher.entity.Teacher;
import com.example.school_management.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final StudentRepository studentRepository;
    private final AdminRepository adminRepository;
    private final EmployeeRepository employeeRepository;
    private final TeacherRepository teacherRepository;
    private final ComplaintMapper complaintMapper;
    private final RoleVerifier roleVerifier; // Assuming you have this utility

    // ── 1. RAISE COMPLAINT (BULK SUPPORT) ────────────────────────────────────
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ComplaintResponseDto> raiseComplaint(ComplaintRequestDto request) {
        String currentUser = roleVerifier.getCurrentUsername();
        boolean isAdmin = roleVerifier.hasRole(Role.ADMIN);

        // 1. Resolve Raiser Identity ONCE to save DB calls
        Admin adminRaiser = null;
        Employee empRaiser = null;

        if (isAdmin) {
            adminRaiser = adminRepository.findByUsername(currentUser)
                    .orElseThrow(() -> new UnauthorizedAccessException("Admin session is invalid."));
        } else {
            empRaiser = employeeRepository.findByEmail(currentUser) // Or findByIdentifier
                    .orElseThrow(() -> new UnauthorizedAccessException("Teacher session is invalid."));
        }

        List<Complaint> complaintsToSave = new ArrayList<>();

        // 2. Loop through selected students and create individual tickets
        for (Long studentId : request.getTargetStudentIds()) {
            Student targetStudent = studentRepository.findById(studentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

            Complaint complaint = complaintMapper.toEntity(request);
            complaint.setTargetStudent(targetStudent);

            if (request.getSupportedMediaUrls() != null) {
                complaint.setSupportedMedia(request.getSupportedMediaUrls());
            }

            if (isAdmin) {
                complaint.setReportedByAdmin(adminRaiser);
            } else {
                complaint.setReportedByEmployee(empRaiser);
            }

            complaintsToSave.add(complaint);
        }

        // 3. Batch Save for performance
        List<Complaint> savedComplaints = complaintRepository.saveAll(complaintsToSave);
        return savedComplaints.stream().map(complaintMapper::toResponseDto).toList();
    }

    // ── 2. VIEW COMPLAINTS RAISED BY ME ──────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<ComplaintResponseDto> getMyRaisedComplaints() {
        String currentUser = roleVerifier.getCurrentUsername();

        List<Complaint> myComplaints = complaintRepository
                .findByReportedByEmployee_EmailOrderByCreatedAtDesc(currentUser);

        return myComplaints.stream().map(complaintMapper::toResponseDto).toList();
    }

    // ── 3. VIEW COMPLAINTS TO RESOLVE (CLASS TEACHER VIEW) ───────────────────
    @Override
    @Transactional(readOnly = true)
    public List<ComplaintResponseDto> getComplaintsForMyClass() {
        String currentUser = roleVerifier.getCurrentUsername();

        Teacher currentTeacher = teacherRepository.findByEmail(currentUser)
                .orElseThrow(() -> new UnauthorizedAccessException("Teacher identity not found."));

        if (currentTeacher.getAssignedClass() == null) {
            // Subject teachers don't have a specific class to manage complaints for
            return new ArrayList<>();
        }

        List<Complaint> classComplaints = complaintRepository
                .findByTargetStudent_AssignedClass_IdOrderByCreatedAtDesc(currentTeacher.getAssignedClass().getId());

        return classComplaints.stream().map(complaintMapper::toResponseDto).toList();
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
    @Override
    @Transactional
    public ComplaintResponseDto updateComplaintVisibility(String ticketNumber, VisibilityUpdateDto request) {
        String currentUser = roleVerifier.getCurrentUsername();
        boolean isAdmin = roleVerifier.hasRole(Role.ADMIN);

        Complaint complaint = complaintRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", "ticketNumber", ticketNumber));

        // Ensure only Admin or the Class Teacher can toggle this
        if (!isAdmin && !isClassTeacher(currentUser, complaint)) {
            throw new UnauthorizedAccessException("Access Denied: Only an Administrator or the Class Teacher can alter visibility.");
        }

        complaint.setVisibleToParents(request.getIsVisibleToParents());
        return complaintMapper.toResponseDto(complaintRepository.save(complaint));
    }
}