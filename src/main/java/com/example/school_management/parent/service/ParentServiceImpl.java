package com.example.school_management.parent.service;

import com.example.school_management.assignment.repository.AssignmentRepository;
import com.example.school_management.attendance.entity.AttendanceConfig;
import com.example.school_management.attendance.repository.AttendanceConfigRepo;
import com.example.school_management.attendance.repository.StudentAttendanceRepo;
import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.common.exception.UnauthorizedAccessException;
import com.example.school_management.notify.dto.ParentNotificationResponseDto;
import com.example.school_management.notify.entity.Notification;
import com.example.school_management.notify.entity.UserNotification;
import com.example.school_management.notify.mapper.UserNotificationMapper;
import com.example.school_management.notify.repository.UserNotificationRepository;
import com.example.school_management.parent.dto.ChildDetailsResponseDto;
import com.example.school_management.parent.dto.ChildSummaryResponseDto;
import com.example.school_management.parent.dto.ParentsDashboardResponseDto;
import com.example.school_management.parent.entity.Parent;
import com.example.school_management.parent.mapper.ParentMapper;
import com.example.school_management.parent.repository.ParentRepository;
import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.student.entity.Student;
import com.example.school_management.student.repository.StudentRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public ParentsDashboardResponseDto getDashboard() {

        String currentUser = roleVerifier.getCurrentUsername();
        Parent parent = parentRepository.findByEmail(currentUser)
                .orElseThrow(() -> new UnauthorizedAccessException("Parent session invalid"));

        Student child = studentRepository.findFirstByParent(parent);

        Float attendancePercent = attendancePercent(child);

        int pendingAssignments = 0;
        if (child.getAssignedClass() != null) {
            pendingAssignments = assignmentRepository
                    .countPendingAssignmentsByClass(child.getAssignedClass(), LocalDateTime.now());
        }

        List<ParentNotificationResponseDto> recentNotifications =
                userNotificationRepository.findTop3ByParentOrderByDeliveredAtDesc(parent)
                        .stream()
                        .map(userNotificationMapper::toParentResponseDto)
                        .collect(Collectors.toList());

        return parentMapper.toDashboardResponseDto(child, attendancePercent, pendingAssignments, recentNotifications);
    }

    @Override
    public ParentsDashboardResponseDto getDashboard(String regId) {
        return null;
    }

    @Override
    public List<ChildSummaryResponseDto> getChildList() {

        String currentUser= roleVerifier.getCurrentUsername();
        Parent parent=parentRepository.findByEmail(currentUser).orElseThrow(() -> new UnauthorizedAccessException("Parent session invalid"));

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

        Student child = studentRepository.findByRegId(regId);

        if (!child.getParent().getId().equals(parent.getId())) {
            throw new UnauthorizedAccessException("You can only view your own child's details");
        }

        AttendanceConfig config = attendanceConfigRepo.findTopByOrderByCreatedAtDesc();
        String academicYear = config.getAcademicYear();

        float percentage = attendancePercent(child);

        return parentMapper.toChildDetailsResponseDto(child, percentage, academicYear);

    }

    // ================= Helper Method ====================

    float attendancePercent(Student child)
    {
        AttendanceConfig config = attendanceConfigRepo.findTopByOrderByCreatedAtDesc();
        String academicYear = config.getAcademicYear();


        long sessionsConducted = studentAttendanceRepo.countTotalSessionsConducted(child.getAssignedClass().getId(), academicYear);

        Integer rollStr = child.getRollNumber();
        long totalAbsent  = studentAttendanceRepo.countAbsentDays(child.getAssignedClass().getId(), academicYear, rollStr);
        long totalPresent = sessionsConducted - totalAbsent;

        float percentage = 0f;
        if (sessionsConducted > 0) {
            percentage = (totalPresent * 100.0f) / sessionsConducted;
        }
        return percentage;
    }

    public Parent getParentById(Long id) {
        return parentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parent not found with id: " + id));
    }
}