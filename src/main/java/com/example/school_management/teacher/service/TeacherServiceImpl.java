package com.example.school_management.teacher.service;

import com.example.school_management.academics.entity.ClassSubjectTeacher;
import com.example.school_management.academics.repository.ClassSubjectTeacherRepository;
import com.example.school_management.assignment.dto.response.TeacherClassDropdownDto;
import com.example.school_management.assignment.mapper.AssignmentMapper;
import com.example.school_management.common.RoleVerifier;
import com.example.school_management.assignment.repository.AssignmentRepository;
import com.example.school_management.attendance.entity.StudentAttendance;
import com.example.school_management.attendance.repository.StudentAttendanceRepo;
import com.example.school_management.common.enums.ComplaintStatus;
import com.example.school_management.common.enums.Role;
import com.example.school_management.common.enums.notify.AudienceType;
import com.example.school_management.common.enums.notify.NotificationType;
import com.example.school_management.common.exception.DuplicateResourceException;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.common.exception.UnauthorizedAccessException;
import com.example.school_management.complaint.entity.Complaint;
import com.example.school_management.complaint.mappper.ComplaintMapper;
import com.example.school_management.complaint.repository.ComplaintRepository;
import com.example.school_management.mail.service.EmailService;
import com.example.school_management.notify.dto.CreateNotificationRequestDto;
import com.example.school_management.notify.dto.NotificationResponseDto;
import com.example.school_management.notify.entity.Notification;
import com.example.school_management.notify.entity.UserNotification;
import com.example.school_management.notify.mapper.NotificationMapper;
import com.example.school_management.notify.repository.NotificationRepository;
import com.example.school_management.notify.repository.UserNotificationRepository;
import com.example.school_management.parent.entity.Parent;
import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.schoolClass.repo.SchoolClassRepository;
import com.example.school_management.student.entity.Student;
import com.example.school_management.student.repository.StudentRepository;
import com.example.school_management.subject.entity.Subject;
import com.example.school_management.subject.repository.SubjectRepository;
import com.example.school_management.teacher.dto.TeacherDashboardResponseDto;
import com.example.school_management.teacher.dto.TeacherFromAdminRequestDto;
import com.example.school_management.teacher.dto.TeacherRemainingInfoDto;
import com.example.school_management.teacher.dto.response.MyClassResponseDto;
import com.example.school_management.teacher.dto.response.TeacherResponseDto;
import com.example.school_management.teacher.entity.Teacher;
import com.example.school_management.teacher.mapper.TeacherMapper;
import com.example.school_management.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;
    private final PasswordEncoder passwordEncoder;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;
    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final StudentAttendanceRepo studentAttendanceRepo;
    private final NotificationMapper notificationMapper;
    private final AssignmentRepository assignmentRepository;
    private final ComplaintRepository complaintRepository;
    private final ComplaintMapper complaintMapper;
    private final EmailService emailService;
    private final RoleVerifier roleVerifier;
    private final ClassSubjectTeacherRepository pivotRepository;
    private final AssignmentMapper assignmentMapper;
    /**
     * Replaces manual SecurityContext verification.
     * Allows access if the user is an ADMIN, OR if they are the specific Teacher.
     */
    private void verifyAdminOrOwnerIdentity(Teacher targetTeacher) {
        if (roleVerifier.hasRole(Role.ADMIN)) {
            return; // Admins bypass all ownership checks
        }

        String currentUsername = roleVerifier.getCurrentUsername();

        // Utilizes the isIdentifiedBy domain logic from Employee.java
        if (!targetTeacher.isIdentifiedBy(currentUsername)) {
            throw new UnauthorizedAccessException("Access Denied: You do not have permission to view or modify this profile.");
        }
    }

    /**
     * Strict verification for Admin-only routes
     */
    private void verifyAdminAccess() {
        if (!roleVerifier.hasRole(Role.ADMIN)) {
            throw new AccessDeniedException("Access Denied: Only Admins can perform this action.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherResponseDto> getUnassignedTeachers() {
        verifyAdminAccess(); // Strictly Admin only

        return teacherRepository.findByAssignedClassIsNullAndIsActiveTrue().stream()
                .map(teacherMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public TeacherResponseDto getTeacherByIdentifier(String identifier) {
        Teacher teacher = teacherRepository.findByEmail(identifier)
                .or(() -> teacherRepository.findByMobile(identifier))
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "identifier", identifier));

        // No ownership check needed here because they are fetching the exact record attached to their login token
        return teacherMapper.toResponseDto(teacher);
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherResponseDto getTeacherById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", id));

        verifyAdminOrOwnerIdentity(teacher); // Validates identity

        return teacherMapper.toResponseDto(teacher);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherResponseDto> getAllTeachers() {
        verifyAdminAccess(); // Secures the bulk fetch endpoint

        return teacherRepository.findAll().stream()
                .map(teacherMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public TeacherResponseDto updateTeacherAfterOnboard(Long teacherId, TeacherRemainingInfoDto request, String username) {

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", teacherId));
        verifyAdminOrOwnerIdentity(teacher);

        teacherMapper.updateTeacherFromRemainingInfoDto(request, teacher);
        if (request.getSubjectIds() != null && !request.getSubjectIds().isEmpty()) {
            List<Subject> subjects = subjectRepository.findAllById(request.getSubjectIds());
            if (subjects.size() != request.getSubjectIds().size()) {
                throw new ResourceNotFoundException("One or more Subject IDs are invalid.");
            }
            teacher.setSubjects(subjects);
        } else if (request.getSubjectIds() != null) {
            teacher.getSubjects().clear();
        }

        Teacher updatedTeacher = teacherRepository.save(teacher);
        return teacherMapper.toResponseDto(updatedTeacher);
    }

    @Override
    @Transactional
    public TeacherResponseDto onboardTeacher(TeacherFromAdminRequestDto request, String username) {

        if (teacherRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Teacher", "email", request.getEmail());
        }
        if (teacherRepository.existsByMobile(request.getMobile())) {
            throw new DuplicateResourceException("Teacher", "mobile", request.getMobile());
        }
        if (teacherRepository.existsByAadharNo(request.getAadharNo())) {
            throw new DuplicateResourceException("Teacher", "aadharNo", request.getAadharNo());
        }
        if (teacherRepository.existsByPanNo(request.getPanNo())) {
            throw new DuplicateResourceException("Teacher", "panNo", request.getPanNo());
        }

        Teacher teacher = teacherMapper.toEntity(request);
        teacher.setRole(Role.TEACHER);
        teacher.setActive(true);
        String namePart = getSafeSubstring(teacher.getLastName(), 3).toUpperCase(Locale.ROOT);
        String mobilePart = getSafeSubstring(teacher.getMobile(), 3);
        String aadharPart = getSafeSubstring(teacher.getAadharNo(), 3);
        String rawPassword = namePart + mobilePart + aadharPart;
        teacher.setPassword(passwordEncoder.encode(rawPassword));

        Teacher savedTeacher = teacherRepository.save(teacher);

//        emailService.sendTeacherOnboardingEmail(
//                savedTeacher.getEmail(),
//                savedTeacher.getFirstName() + " " + savedTeacher.getLastName(),
//                rawPassword
//        );

        return teacherMapper.toResponseDto(savedTeacher);
    }

    @Override
    @Transactional
    public List<TeacherResponseDto> onboardTeachersBulk(List<TeacherFromAdminRequestDto> requests, String username) {

        List<Teacher> teachersToSave = new ArrayList<>();
        List<String> generatedPasswords = new ArrayList<>(); // Track passwords for emails

        for (TeacherFromAdminRequestDto request : requests) {
            // 1. Validation Checks
            if (teacherRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Teacher", "email", request.getEmail());
            }
            if (teacherRepository.existsByMobile(request.getMobile())) {
                throw new DuplicateResourceException("Teacher", "mobile", request.getMobile());
            }
            if (teacherRepository.existsByAadharNo(request.getAadharNo())) {
                throw new DuplicateResourceException("Teacher", "aadharNo", request.getAadharNo());
            }
            if (teacherRepository.existsByPanNo(request.getPanNo())) {
                throw new DuplicateResourceException("Teacher", "panNo", request.getPanNo());
            }

            // 2. Mapping and Defaults
            Teacher teacher = teacherMapper.toEntity(request);
            teacher.setRole(Role.TEACHER);
            teacher.setActive(true);

            // 3. Password Generation
            String namePart = getSafeSubstring(teacher.getLastName(), 3).toUpperCase(Locale.ROOT);
            String mobilePart = getSafeSubstring(teacher.getMobile(), 3);
            String aadharPart = getSafeSubstring(teacher.getAadharNo(), 3);
            String rawPassword = namePart + mobilePart + aadharPart;

            teacher.setPassword(passwordEncoder.encode(rawPassword));

            teachersToSave.add(teacher);
            generatedPasswords.add(rawPassword);
        }

        // 4. Batch Save to Database
        List<Teacher> savedTeachers = teacherRepository.saveAll(teachersToSave);

        // 5. Dispatch Emails and Map Responses
        List<TeacherResponseDto> responses = new ArrayList<>();
        for (int i = 0; i < savedTeachers.size(); i++) {
            Teacher savedTeacher = savedTeachers.get(i);
            String rawPassword = generatedPasswords.get(i);

            emailService.sendTeacherOnboardingEmail(
                    savedTeacher.getEmail(),
                    savedTeacher.getFirstName() + " " + savedTeacher.getLastName(),
                    rawPassword
            );

            responses.add(teacherMapper.toResponseDto(savedTeacher));
        }

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public MyClassResponseDto getMyAssignedClass() {
        String currentUsername = roleVerifier.getCurrentUsername();

        // Fetch the teacher (Adjust the repository call to match your exact method, e.g., findByEmail)
        Teacher teacher = teacherRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("Logged in teacher not found in database"));

        // If the teacher is just a subject teacher and has no specific class, return null data
        if (teacher.getAssignedClass() == null) {
            return null;
        }

        String className = teacher.getAssignedClass().getAcademicStructure().getStandard() + "-" +
                teacher.getAssignedClass().getAcademicStructure().getDivision();

        return MyClassResponseDto.builder()
                .classId(teacher.getAssignedClass().getId())
                .className(className)
                .teacherId(teacher.getId())
                .teacherName(teacher.getFirstName() + " " + teacher.getLastName())
                .build();
    }


    @Override
    public NotificationResponseDto notifyParentsOfClass(long classId, CreateNotificationRequestDto notification) throws AccessDeniedException{

        if(!roleVerifier.hasRole(Role.TEACHER))
        {
            throw new AccessDeniedException("Only teachers can access this service");
        }
        return notifyParents(classId, notification);
    }

    @Override
    public List<NotificationResponseDto> getMyNotifications() {
        String username = roleVerifier.getCurrentUsername();

        Teacher teacher = teacherRepository.findByEmail(username)
                .orElseThrow(() -> new UnauthorizedAccessException("Teacher session invalid"));

        return notificationRepository.findByCreatedByTeacher(teacher)
                .stream()
                .map(notificationMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public TeacherDashboardResponseDto getDashboard(Long teacherId) {

        String currentUser = roleVerifier.getCurrentUsername();
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", teacherId));

        if (!teacher.isIdentifiedBy(currentUser)) {
            throw new UnauthorizedAccessException("You can only view your own dashboard");
        }

        SchoolClass assignedClass = teacher.getAssignedClass();
        Float attendancePercent = null;
        if (assignedClass != null) {
            List<StudentAttendance> attendances = studentAttendanceRepo
                    .findByClassEntity(assignedClass);
            int totalDays = attendances.size();
            int totalStudents = assignedClass.getTotalStudents();

            if (totalDays > 0 && totalStudents > 0) {
                int totalAbsences = attendances.stream()
                        .mapToInt(a -> a.getAbsentRollNumbers().size())
                        .sum();
                int totalPossible = totalDays * totalStudents;
                attendancePercent = ((totalPossible - totalAbsences) * 100.0f) / totalPossible;
            }
        }

        int activeAssignments = assignmentRepository
                .countActiveAssignmentsByTeacher(teacher, LocalDateTime.now());
        List<Complaint> openComplaints = new ArrayList<>();
        if(assignedClass != null){
            openComplaints = complaintRepository.findByTargetStudent_AssignedClass_IdAndStatusOrderByCreatedAtDesc(assignedClass.getId(),ComplaintStatus.PENDING);
        }
        int subjectsTaught = pivotRepository.countDistinctSubjectsByTeacherId(teacher.getId());

        return TeacherDashboardResponseDto.builder()
                .attendancePercent(attendancePercent)
                .activeAssignments(activeAssignments)
                .openComplaints(openComplaints.size())
                .subjectsTaught(subjectsTaught)
                .complaints(openComplaints.stream()
                        .map(complaintMapper::toResponseDto)
                        .collect(Collectors.toList()))
                .build();
    }

// ===================== HELPER METHODS ===========================

    private void notifyToCompleteProfile(Teacher savedTeacher) {

        Notification notification = Notification.builder()
                .title("Complete Profile")
                .description("Attention "+ savedTeacher.getFirstName() +" complete the remaining profile from your login")
                .shortInfo("Complete the profile")
                .audienceType(AudienceType.TEACHER)
                .notificationType(NotificationType.UPDATE)
                .eventDateTime(LocalDateTime.now()) // immediate event
                .scheduledDispatchTime(LocalDateTime.now())
                .dispatched(true)
                .build();

        notificationRepository.save(notification);

        UserNotification userNotification = UserNotification.builder()
                .notification(notification)
                .teacher(savedTeacher)
                .deliveredAt(LocalDateTime.now())
                .build();

        userNotificationRepository.save(userNotification);
    }

    private NotificationResponseDto notifyParents(long classId, CreateNotificationRequestDto request) {

        SchoolClass schoolClass = schoolClassRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class", "id", classId));

        Set<SchoolClass> targetClasses = new HashSet<>();
        targetClasses.add(schoolClass);

        String currentUser= roleVerifier.getCurrentUsername();

        Teacher teacher = teacherRepository.findByEmail(currentUser)
                .orElseThrow(() -> new UnauthorizedAccessException("Teacher session invalid"));

        Notification notification = Notification.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .shortInfo(request.getShortInfo())
                .audienceType(AudienceType.PARENT)
                .notificationType(request.getNotificationType())
                .eventDateTime(request.getEventDateTime())
                .scheduledDispatchTime(Boolean.TRUE.equals(request.getDispatchNow())
                        ? LocalDateTime.now()
                        : null)
                .dispatched(false)
                .targetClasses(targetClasses)
                .teacherIds(new HashSet<>())
                .createdByTeacher(teacher)
                .build();

        notificationRepository.save(notification);

        if (Boolean.TRUE.equals(request.getDispatchNow())) {

            List<Student> students = studentRepository.findByAssignedClass_Id(classId);

            for (Student student : students) {
                Parent parent = student.getParent();

                if (parent == null) continue;

                UserNotification userNotification = UserNotification.builder()
                        .notification(notification)
                        .parent(parent)
                        .isRead(false)
                        .deliveredAt(LocalDateTime.now())
                        .build();

                userNotificationRepository.save(userNotification);
            }

            notification.setDispatched(true);
            notificationRepository.save(notification);
        }

        return notificationMapper.toResponseDto(notification);
    }

    private String getSafeSubstring(String value, int length) {
        if (value == null) return "";
        return value.length() < length ? value : value.substring(0, length);
    }

    @Transactional(readOnly = true)
    public List<TeacherClassDropdownDto> getMyAllocations() {
        String identifier = roleVerifier.getCurrentUsername();
        Teacher currentTeacher = teacherRepository.findByEmail(identifier)
                .or(() -> teacherRepository.findByMobile(identifier))
                .orElseThrow(() -> new AccessDeniedException("Teacher profile not found."));

        List<ClassSubjectTeacher> myAllocations = pivotRepository.findAllByTeacherId(currentTeacher.getId());
        return assignmentMapper.toClassDropdownList(myAllocations);
    }
}