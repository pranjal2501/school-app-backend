package com.example.school_management.parent.service;

import com.example.school_management.assignment.repository.AssignmentRepository;
import com.example.school_management.attendance.entity.AttendanceConfig;
import com.example.school_management.attendance.repository.AttendanceConfigRepo;
import com.example.school_management.attendance.repository.StudentAttendanceRepo;
import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.common.exception.UnauthorizedAccessException;
import com.example.school_management.notify.dto.ParentNotificationResponseDto;
import com.example.school_management.notify.mapper.UserNotificationMapper;
import com.example.school_management.notify.repository.UserNotificationRepository;
import com.example.school_management.parent.dto.ChildDetailsResponseDto;
import com.example.school_management.parent.dto.ChildSummaryResponseDto;
import com.example.school_management.parent.dto.ParentProfileResponseDto;
import com.example.school_management.parent.dto.ParentsDashboardResponseDto;
import com.example.school_management.parent.entity.Parent;
import com.example.school_management.parent.mapper.ParentMapper;
import com.example.school_management.parent.repository.ParentRepository;
import com.example.school_management.student.entity.Student;
import com.example.school_management.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParentServiceImpl implements ParentService {

    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;
    private final StudentAttendanceRepo studentAttendanceRepo;
    private final AttendanceConfigRepo attendanceConfigRepo;
    private final UserNotificationRepository userNotificationRepository;
    private final AssignmentRepository assignmentRepository;
    private final ParentMapper parentMapper;
    private final UserNotificationMapper userNotificationMapper;
    private final RoleVerifier roleVerifier;

    @Override
    public ParentsDashboardResponseDto getDashboard(String registrationId) {
        String currentUser = roleVerifier.getCurrentUsername();
        Parent parent = parentRepository.findByEmail(currentUser)
                .orElseThrow(() -> new UnauthorizedAccessException("Parent session invalid"));

        Student child = studentRepository.findByRegId(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (!child.getParent().getId().equals(parent.getId())) {
            throw new UnauthorizedAccessException("You do not have access to this student's data");
        }

        boolean isClassAllocated = child.getAssignedClass() != null;
        boolean isProfileComplete = child.getGender() != null
                && child.getNationality() != null
                && child.getReligion() != null
                && child.getCategory() != null;

        Float attendancePercent = 0f;
        int pendingAssignments = 0;

        if (isClassAllocated && isProfileComplete) {
            attendancePercent = attendancePercent(child);
            pendingAssignments = assignmentRepository
                    .countPendingAssignmentsByClass(child.getAssignedClass(), LocalDateTime.now());
        }

        List<ParentNotificationResponseDto> recentNotifications = userNotificationRepository
                .findTop3ByParentOrderByDeliveredAtDesc(parent)
                .stream()
                .map(userNotificationMapper::toParentResponseDto)
                .collect(Collectors.toList());

        return parentMapper.toDashboardResponseDto(
                child, parent, isClassAllocated, isProfileComplete,
                attendancePercent, pendingAssignments, recentNotifications
        );
    }

    @Override
    public List<ChildSummaryResponseDto> getChildList() {
        String currentUser = roleVerifier.getCurrentUsername();
        Parent parent = parentRepository.findByEmail(currentUser)
                .orElseThrow(() -> new UnauthorizedAccessException("Parent session invalid"));

        return parent.getStudents()
                .stream()
                .map(parentMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    public ChildDetailsResponseDto getChildDetails(String regId) {
        String currentUser = roleVerifier.getCurrentUsername();
        Parent parent = parentRepository.findByEmail(currentUser)
                .orElseThrow(() -> new UnauthorizedAccessException("Parent session invalid"));

        Student child = studentRepository.findByRegId(regId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with given registration id."));

        if (!child.getParent().getId().equals(parent.getId())) {
            throw new UnauthorizedAccessException("You can only view your own child's details");
        }

        AttendanceConfig config = attendanceConfigRepo.findTopByOrderByCreatedAtDesc();
        String academicYear = config != null ? config.getAcademicYear() : "N/A";
        float percentage = attendancePercent(child);

        // 🚀 DELEGATED TO MAPPER (Reduced 35 lines of code down to 1 line)
        return parentMapper.toChildDetailsResponseDto(child, percentage, academicYear);
    }

    // ================= Helper Method ====================

    private float attendancePercent(Student child) {
        if (child.getAssignedClass() == null) return 0f;
        if (child.getRollNumber() == null) return 0f;

        AttendanceConfig config = attendanceConfigRepo.findTopByOrderByCreatedAtDesc();
        if (config == null) return 0f;

        String academicYear = config.getAcademicYear();
        Long classId = child.getAssignedClass().getId();
        Integer rollNumber = child.getRollNumber();

        long sessionsConducted = studentAttendanceRepo.countTotalSessionsConducted(classId, academicYear);
        if (sessionsConducted == 0) return 0f;

        long totalAbsent = studentAttendanceRepo.countAbsentDays(classId, academicYear, rollNumber);
        long totalPresent = sessionsConducted - totalAbsent;

        return (totalPresent * 100.0f) / sessionsConducted;
    }

    public Parent getParentById(Long id) {
        return parentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parent not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public ParentProfileResponseDto getMyProfile() {
        String currentUser = roleVerifier.getCurrentUsername();

        Parent parent = parentRepository.findByEmail(currentUser)
                .or(() -> parentRepository.findByMobilePrimary(currentUser))
                .orElseThrow(() -> new UnauthorizedAccessException("Parent session invalid"));

        return parentMapper.toProfileDto(parent);
    }
}