package com.example.school_management.assignment.service;

import com.example.school_management.academics.entity.ClassSubjectTeacher;
import com.example.school_management.academics.repository.ClassSubjectTeacherRepository;
import com.example.school_management.assignment.dto.request.*;
import com.example.school_management.assignment.dto.response.AssignmentResponseDto;
import com.example.school_management.assignment.dto.response.SubmissionResponseDto;
import com.example.school_management.assignment.dto.response.TeacherClassDropdownDto;
import com.example.school_management.assignment.entity.Assignment;
import com.example.school_management.assignment.entity.AssignmentSubmission;
import com.example.school_management.assignment.mapper.AssignmentMapper;
import com.example.school_management.assignment.mapper.SubmissionMapper;
import com.example.school_management.assignment.repository.AssignmentRepository;
import com.example.school_management.assignment.repository.AssignmentSubmissionRepository;
import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.enums.Role;
import com.example.school_management.common.enums.notify.AudienceType;
import com.example.school_management.common.enums.notify.NotificationType;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.common.exception.UnauthorizedAccessException;
import com.example.school_management.student.entity.Student;
import com.example.school_management.student.repository.StudentRepository;
import com.example.school_management.notify.entity.Notification;
import com.example.school_management.notify.entity.UserNotification;
import com.example.school_management.notify.repository.NotificationRepository;
import com.example.school_management.notify.repository.UserNotificationRepository;
import com.example.school_management.parent.entity.Parent;
import com.example.school_management.teacher.entity.Teacher;
import com.example.school_management.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService{

    private final AssignmentRepository assignmentRepository;
    private final ClassSubjectTeacherRepository pivotRepository;
    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final TeacherRepository teacherRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final StudentRepository studentRepository;
    private final SubmissionMapper submissionMapper;
    private final AssignmentMapper assignmentMapper;
    private final RoleVerifier roleVerifier;

    private Teacher getCurrentTeacher() {
        if (!roleVerifier.hasRole(Role.TEACHER)) {
            throw new AccessDeniedException("Only teachers can perform this action.");
        }
        String username = roleVerifier.getCurrentUsername();
        return teacherRepository.findByEmail(username)
                .or(() -> teacherRepository.findByMobile(username))
                .orElseThrow(() -> new AccessDeniedException("Teacher profile not found."));
    }

    @Transactional
    public List<AssignmentResponseDto> createBulkAssignments(List<AssignmentBulkRequestDto> requests) {
        Teacher currentTeacher = getCurrentTeacher();
        List<Assignment> assignmentsToSave = new ArrayList<>();

        for (AssignmentBulkRequestDto request : requests) {
            for (Long classId : request.getClassIds()) {

                // 1. Check if the Subject is actually taught in this Class
                ClassSubjectTeacher allocation = pivotRepository
                        .findBySchoolClassIdAndSubjectId(classId, request.getSubjectId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Allocation", "Class/Subject", classId + "/" + request.getSubjectId()));

                // 2. STRICT DOMAIN CHECK: Is THIS teacher the one teaching it?
                if (!allocation.getTeacher().getId().equals(currentTeacher.getId())) {
                    throw new AccessDeniedException(
                            "You are not authorized to assign work to class ID " + classId +
                                    ". The designated teacher is " + allocation.getTeacher().getFirstName() +
                                    " " + allocation.getTeacher().getLastName());
                }

                // 3. Map to Entity (Requires a minor update to your mapper to accept the new DTO)
                Assignment assignment = assignmentMapper.toEntity(request, allocation);
                assignmentsToSave.add(assignment);
            }
        }

        // 4. Batch Save for high performance
        List<Assignment> savedAssignments = assignmentRepository.saveAll(assignmentsToSave);

        // 5. Map all saved entities back to response DTOs
        return savedAssignments.stream()
                .map(assignmentMapper::toResponseDto)
                .toList();
    }

    /**
     * Powers the UI Dropdown so the Teacher knows exactly what classes they can assign work to.
     */
    @Transactional(readOnly = true)
    public List<TeacherClassDropdownDto> getMyAllocations() {
        Teacher currentTeacher = getCurrentTeacher();

        List<ClassSubjectTeacher> myAllocations = pivotRepository.findAllByTeacherId(currentTeacher.getId());

        // Pass the entire list to the mapper for grouping
        return assignmentMapper.toClassDropdownList(myAllocations);
    }


    @Transactional
    public List<SubmissionResponseDto> upsertSubmissions(Long assignmentId, SubmissionBulkRequestDto request) {
        Teacher currentTeacher = getCurrentTeacher();

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", assignmentId));

        if (!assignment.getAllocation().getTeacher().getId().equals(currentTeacher.getId())) {
            throw new AccessDeniedException("You are not authorized to mark submissions for this assignment.");
        }

        List<AssignmentSubmission> submissionsToSave = new ArrayList<>();

        for (StudentSubmissionDto dto : request.getSubmissions()) {
            Student student = studentRepository.findById(dto.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student", "id", dto.getStudentId()));

            // Find existing or create new
            AssignmentSubmission submission = submissionRepository
                    .findByAssignmentIdAndStudentId(assignmentId, student.getId())
                    .orElse(submissionMapper.toEntity(dto, assignment, student));

            // Update status if it already existed
            submission.setSubmitted(dto.getIsSubmitted());
            submissionsToSave.add(submission);
        }

        List<AssignmentSubmission> saved = submissionRepository.saveAll(submissionsToSave);
        return saved.stream().map(submissionMapper::toResponseDto).toList();
    }

    @Transactional(readOnly = true)
    public List<SubmissionResponseDto> getSubmissionsForAssignment(Long assignmentId) {
        Teacher currentTeacher = getCurrentTeacher();
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", assignmentId));

        if (!assignment.getAllocation().getTeacher().getId().equals(currentTeacher.getId())) {
            throw new AccessDeniedException("You are not authorized to view submissions for this assignment.");
        }

        // 1. Get the specific class for this assignment
        Long classId = assignment.getAllocation().getSchoolClass().getId();

        // 2. Fetch ALL active students in this class
        // (Ensure you have a method like this in your StudentRepository)
        List<Student> allStudentsInClass = studentRepository.findAllByAssignedClassIdAndIsActiveTrue(classId);

        // 3. Fetch existing submissions for this assignment
        List<AssignmentSubmission> existingSubmissions = submissionRepository.findAllByAssignmentId(assignmentId);

        // 4. Map existing submissions by Student ID for fast lookup
        Map<Long, Boolean> submissionMap = existingSubmissions.stream()
                .collect(Collectors.toMap(sub -> sub.getStudent().getId(), AssignmentSubmission::isSubmitted));

        // 5. Merge and return the complete list
        return allStudentsInClass.stream().map(student -> SubmissionResponseDto.builder()
                .studentId(student.getId())
                .studentName(student.getFirstName() + " " + student.getLastName())
                .rollNumber(student.getRollNumber())
                // If a submission exists, use its status. Otherwise, default to false.
                .isSubmitted(submissionMap.getOrDefault(student.getId(), false))
                .build()
        ).toList();
    }

    @Transactional
    public AssignmentResponseDto createAssignment(AssignmentRequestDto request) {
        Teacher currentTeacher = getCurrentTeacher();

        // 1. Check if the Subject is actually taught in this Class (Triggers 404 Not Found)
        ClassSubjectTeacher allocation = pivotRepository
                .findBySchoolClassIdAndSubjectId(request.getClassId(), request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Allocation", "Class/Subject", request.getClassId() + "/" + request.getSubjectId()));

        // 2. STRICT DOMAIN CHECK: Is THIS teacher the one teaching it? (Triggers 403 Forbidden)
        if (!allocation.getTeacher().getId().equals(currentTeacher.getId())) {
            throw new AccessDeniedException(
                    "You are not authorized to assign work to this class. The designated teacher is "
                            + allocation.getTeacher().getFirstName() + " " + allocation.getTeacher().getLastName());
        }

        // 3. Map and Save
        Assignment assignment = assignmentMapper.toIndividualEntity(request, allocation);
        Assignment saved = assignmentRepository.save(assignment);
        assignmentRepository.flush();

        notifyParentsOnNewAssignment(saved);

        return assignmentMapper.toResponseDto(saved);
    }

    @Transactional
    public AssignmentResponseDto updateAssignment(Long assignmentId, AssignmentUpdateRequestDto request) {
        Teacher currentTeacher = getCurrentTeacher();

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", assignmentId));

        // 🔒 SECURITY: Ensure this teacher owns the assignment
        if (!assignment.getAllocation().getTeacher().getId().equals(currentTeacher.getId())) {
            throw new AccessDeniedException("You are not authorized to edit this assignment.");
        }

        assignment.setTitle(request.getTitle());
        assignment.setDescription(request.getDescription());
        assignment.setDocumentUrl(request.getDocumentUrl());
        assignment.setDueDate(request.getDueDate());

        Assignment updated = assignmentRepository.save(assignment);
        return assignmentMapper.toResponseDto(updated);
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponseDto> getAssignmentsForClass(Long classId) {
        // Anyone authenticated can view assignments (Students, Parents, Teachers, Admins)
        return assignmentRepository.findAllByAllocation_SchoolClass_Id(classId)
                .stream()
                .map(assignmentMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteAssignment(Long id) {
        String currentUser = roleVerifier.getCurrentUsername();
        boolean isAdmin = roleVerifier.hasRole(Role.ADMIN);

        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", id));

        if (!isAdmin) {
            Teacher currentTeacher = teacherRepository.findByEmail(currentUser)
                    .orElseThrow(() -> new UnauthorizedAccessException("Teacher identity not found."));

            // Assuming your Assignment entity is mapped to a Teacher
            if (!assignment.getAllocation().getTeacher().getId().equals(currentTeacher.getId())) {
                throw new UnauthorizedAccessException("Access Denied: You can only delete assignments that you have created.");
            }
        }

        // Delete associated files/submissions if necessary, then delete the assignment
        assignmentRepository.delete(assignment);
    }

    private void notifyParentsOnNewAssignment(Assignment assignment) {

        // 1. Create Notification
        Notification notification = Notification.builder()
                .title(assignment.getTitle())
                .description("Assignment : " +assignment.getDescription())
                .shortInfo("New Assignment of "+assignment.getAssignmentType()+" is expected to be completed within "+ assignment.getDueDate())
                .audienceType(AudienceType.PARENT)
                .notificationType(NotificationType.UPDATE)
                .eventDateTime(LocalDateTime.now()) // immediate event
                .scheduledDispatchTime(LocalDateTime.now())
                .dispatched(true)
                .build();

        notificationRepository.save(notification);

        List<Student> studentsInClass = assignment.getAllocation().getSchoolClass().getStudents();

        for (Student student : studentsInClass) {
            Parent parent = student.getParent();
            if (parent == null) continue;  // skip if student has no parent linked

            UserNotification userNotification = UserNotification.builder()
                    .notification(notification)
                    .parent(parent)
                    .deliveredAt(LocalDateTime.now())
                    .build();

            userNotificationRepository.save(userNotification);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponseDto> getMyCreatedAssignments() {
        Teacher currentTeacher = getCurrentTeacher();

        return assignmentRepository.findAllByAllocation_Teacher_IdOrderByCreatedAtDesc(currentTeacher.getId())
                .stream()
                .map(assignmentMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<AssignmentResponseDto> getStudentAssignmentsWithStatus(String regId) {
        // 1. Get Student & Verify Parent Access (omitted for brevity, similar to previous logic)
        Student student = studentRepository.findByRegId(regId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (student.getAssignedClass() == null) {
            return List.of(); // Return empty if no class assigned
        }

        // 2. Fetch all assignments for the student's class
        List<Assignment> assignments = assignmentRepository
                .findByAllocation_SchoolClass_IdOrderByDueDateAsc(student.getAssignedClass().getId());

        // 3. Map to DTO while checking submission status
        return assignments.stream().map(a -> {
            // Check if a submission entry exists for this student and this assignment
            boolean submitted = submissionRepository
                    .existsByAssignmentIdAndStudentIdAndIsSubmittedTrue(a.getId(), student.getId());

            return AssignmentResponseDto.builder()
                    .id(a.getId())
                    .title(a.getTitle())
                    .description(a.getDescription())
                    .documentUrl(a.getDocumentUrl())
                    .createdAt(a.getCreatedAt())
                    .assignmentType(a.getAssignmentType())
                    .dueDate(a.getDueDate())
                    .totalMarks(a.getTotalMarks())
                    .className(a.getAllocation().getSchoolClass().getClassName())
                    .classId(a.getAllocation().getSchoolClass().getId())
                    .subjectName(a.getAllocation().getSubject().getSubjectName())
                    .subjectId(a.getAllocation().getSubject().getId())
                    .teacherName(a.getAllocation().getTeacher().getFirstName() + " " + a.getAllocation().getTeacher().getLastName())
                    .isSubmitted(submitted)
                    .build();
        }).collect(Collectors.toList());
    }
}