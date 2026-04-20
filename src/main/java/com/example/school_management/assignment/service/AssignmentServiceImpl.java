package com.example.school_management.assignment.service;

import com.example.school_management.academics.dto.response.AssignmentResponseDto;
import com.example.school_management.academics.entity.ClassSubjectTeacher;
import com.example.school_management.academics.repository.ClassSubjectTeacherRepository;
import com.example.school_management.assignment.dto.request.AssignmentRequestDto;
import com.example.school_management.assignment.dto.response.TeacherClassDropdownDto;
import com.example.school_management.assignment.entity.Assignment;
import com.example.school_management.assignment.mapper.AssignmentMapper;
import com.example.school_management.assignment.repository.AssignmentRepository;
import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.enums.Role;
import com.example.school_management.common.enums.notify.AudienceType;
import com.example.school_management.common.enums.notify.NotificationType;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.notify.entity.Notification;
import com.example.school_management.notify.entity.UserNotification;
import com.example.school_management.notify.repository.NotificationRepository;
import com.example.school_management.notify.repository.UserNotificationRepository;
import com.example.school_management.parent.entity.Parent;
import com.example.school_management.student.entity.Student;
import com.example.school_management.student.repository.StudentRepository;
import com.example.school_management.teacher.entity.Teacher;
import com.example.school_management.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService{

    private final AssignmentRepository assignmentRepository;
    private final ClassSubjectTeacherRepository pivotRepository;
    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
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
        Assignment assignment = assignmentMapper.toEntity(request, allocation);
        Assignment saved = assignmentRepository.save(assignment);
        assignmentRepository.flush();

        notifyParentsOnNewAssignment(saved);

        return assignmentMapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponseDto> getAssignmentsForClass(Long classId) {
        // Anyone authenticated can view assignments (Students, Parents, Teachers, Admins)
        return assignmentRepository.findAllByAllocation_SchoolClass_Id(classId)
                .stream()
                .map(assignmentMapper::toResponseDto)
                .toList();
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
}