package com.example.school_management.complaint;

import com.example.school_management.admin.Admin;
import com.example.school_management.admin.AdminRepository;
import com.example.school_management.common.enums.ComplaintStatus;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.common.exception.UnauthorizedAccessException;
import com.example.school_management.complaint.Complaint;
import com.example.school_management.complaint.dto.*;
import com.example.school_management.employee.Employee;
import com.example.school_management.employee.EmployeeRepository;
import com.example.school_management.student.Student;
import com.example.school_management.student.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final StudentRepository studentRepository;
    private final EmployeeRepository employeeRepository;
    private final ComplaintMapper complaintMapper;
    private final AdminRepository adminRepository;

    // @Transactional ensures that if ANY step fails, the entire database operation rolls back.
    // Isolation.READ_COMMITTED prevents reading "dirty" uncommitted data from other concurrent transactions.
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ComplaintResponseDto raiseComplaint(ComplaintRequestDto request, String userIdentifier, boolean isAdmin) {

        // 1. Strict Validation: Target student
        Student targetStudent = studentRepository.findById(request.getTargetStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getTargetStudentId()));

        // 2. Mapping
        Complaint complaint = complaintMapper.toEntity(request);
        complaint.setTargetStudent(targetStudent);

        // 3. Smart Raiser Resolution
        if (isAdmin) {
            Admin adminRaiser = adminRepository.findByUsername(userIdentifier)
                    .orElseThrow(() -> new UnauthorizedAccessException("Admin session is invalid."));
            complaint.setReportedByAdmin(adminRaiser);
        } else {
            Employee empRaiser = employeeRepository.findByIdentifier(userIdentifier)
                    .orElseThrow(() -> new UnauthorizedAccessException("Teacher session is invalid."));
            complaint.setReportedByEmployee(empRaiser);
        }

        // 4. Save and Return
        return complaintMapper.toResponseDto(complaintRepository.save(complaint));
    }

    @Override
    @Transactional
    public ComplaintResponseDto updateComplaintStatus(String ticketNumber, ComplaintStatusUpdateDto request, String userIdentifier, boolean isAdmin) {

        Complaint complaint = complaintRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", "ticketNumber", ticketNumber));

        // 1. Clean, Domain-Driven Ownership Check
        Employee reporter = complaint.getReportedByEmployee();
        boolean isOwner = reporter != null && reporter.isIdentifiedBy(userIdentifier);

        // Strict Access Check: Only Admins OR the specific Teacher who raised it can update it
        if (!isAdmin && !isOwner) {
            throw new UnauthorizedAccessException("You do not have permission to modify this complaint.");
        }

        // Strict State Machine Logic: Prevent illegal status transitions
        // If resolving, record exactly who resolved it
        if (request.getNewStatus() == ComplaintStatus.RESOLVED) {
            if (isAdmin) {
                Admin adminResolver = adminRepository.findByUsername(userIdentifier)
                        .orElseThrow(() -> new UnauthorizedAccessException("Admin identity could not be verified."));
                complaint.setReportedByAdmin(adminResolver);
            } else {
                Employee employeeResolver = employeeRepository.findByIdentifier(userIdentifier)
                        .orElseThrow(() -> new UnauthorizedAccessException("Teacher identity could not be verified."));
                complaint.setResolvedBy(employeeResolver);
            }
        }

        if (request.getNewStatus() == ComplaintStatus.RESOLVED && request.getResolutionComment() == null) {
            throw new IllegalArgumentException("A resolution comment is strictly required when resolving a complaint.");
        }

        // Apply changes
        complaint.setStatus(request.getNewStatus());
        if (request.getResolutionComment() != null) {
            complaint.setResolutionComment(request.getResolutionComment());
        }

        // If resolving, record who resolved it
        if (request.getNewStatus() == ComplaintStatus.RESOLVED) {
            Employee resolver = employeeRepository.findByIdentifier(userIdentifier)
                    .orElseThrow(() -> new UnauthorizedAccessException("Resolver identity could not be verified."));
            complaint.setResolvedBy(resolver);
        }

        return complaintMapper.toResponseDto(complaintRepository.save(complaint));
    }
}