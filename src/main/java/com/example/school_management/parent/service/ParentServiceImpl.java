//package com.example.school_management.parent.service;
//
//import com.example.school_management.assignment.repository.AssignmentRepository;
//import com.example.school_management.attendance.entity.AttendanceConfig;
//import com.example.school_management.attendance.repository.AttendanceConfigRepo;
//import com.example.school_management.attendance.repository.StudentAttendanceRepo;
//import com.example.school_management.common.RoleVerifier;
//import com.example.school_management.common.exception.ResourceNotFoundException;
//import com.example.school_management.common.exception.UnauthorizedAccessException;
//import com.example.school_management.notify.dto.ParentNotificationResponseDto;
//import com.example.school_management.notify.mapper.UserNotificationMapper;
//import com.example.school_management.notify.repository.UserNotificationRepository;
//import com.example.school_management.parent.dto.ChildDetailsResponseDto;
//import com.example.school_management.parent.dto.ChildSummaryResponseDto;
//import com.example.school_management.parent.dto.ParentsDashboardResponseDto;
//import com.example.school_management.parent.entity.Parent;
//import com.example.school_management.parent.mapper.ParentMapper;
//import com.example.school_management.parent.repository.ParentRepository;
//import com.example.school_management.student.entity.Student;
//import com.example.school_management.student.repository.StudentRepository;
//import com.example.school_management.teacher.entity.Teacher;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class ParentServiceImpl implements ParentService {
//
//    private final ParentRepository parentRepository;
//    private final StudentRepository studentRepository;
//    private final StudentAttendanceRepo studentAttendanceRepo;
//    private final AttendanceConfigRepo attendanceConfigRepo;
//    private final UserNotificationRepository userNotificationRepository;
//    private final AssignmentRepository assignmentRepository;
//    private final ParentMapper parentMapper;
//    private final UserNotificationMapper userNotificationMapper;
//    private final RoleVerifier roleVerifier;
//
//
//    public ParentsDashboardResponseDto getDashboard(String registrationId) {
//        String currentUser = roleVerifier.getCurrentUsername();
//        Parent parent = parentRepository.findByEmail(currentUser)
//                .orElseThrow(() -> new UnauthorizedAccessException("Parent session invalid"));
//
//        // Fetch the specific child using the Registration ID
//        Student child = studentRepository.findByRegId(registrationId)
//                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
//
//        // Security check
//        if (!child.getParent().getId().equals(parent.getId())) {
//            throw new UnauthorizedAccessException("You do not have access to this student's data");
//        }
//
//        boolean isClassAllocated = child.getAssignedClass() != null;
//        boolean isProfileComplete = child.getGender() != null
//                && child.getNationality() != null
//                && child.getReligion() != null
//                && child.getCategory() != null;
//
//        Float attendancePercent = 0f;
//        int pendingAssignments = 0;
//        List<ParentNotificationResponseDto> recentNotifications = List.of();
//
//        if (isClassAllocated && isProfileComplete) {
//            attendancePercent = attendancePercent(child);
//            pendingAssignments = assignmentRepository
//                    .countPendingAssignmentsByClass(child.getAssignedClass(), LocalDateTime.now());
//        }
//
//        recentNotifications = userNotificationRepository.findTop3ByParentOrderByDeliveredAtDesc(parent)
//                .stream()
//                .map(userNotificationMapper::toParentResponseDto)
//                .collect(Collectors.toList());
//    }
//
//        return ParentsDashboardResponseDto.builder()
//                .
//
//    parentServiceImpl(child.getId())
//            .
//
//    parentServiceImpl(parent.getFatherFirstName() + " " + parent.getFatherLastName())
//            .
//
//    parentServiceImpl(child.getFirstName() + " " + child.getLastName())
//            .
//
//    parentServiceImpl(isClassAllocated ? child.getAssignedClass().
//
//    parentServiceImpl() : "Unassigned")
//            .
//
//    parentServiceImpl(isClassAllocated)
//                .
//
//    parentServiceImpl(isProfileComplete)
//                .
//
//    parentServiceImpl(attendancePercent)
//                .
//
//    parentServiceImpl(pendingAssignments)
//                .
//
//    parentServiceImpl(recentNotifications)
//
//}
//
//@Override
//public List<ChildSummaryResponseDto> getChildList() {
//
//    String currentUser= roleVerifier.getCurrentUsername();
//    Parent parent=parentRepository.findByEmail(currentUser).orElseThrow(() -> new UnauthorizedAccessException("Parent session invalid"));
//
//    return parent.getStudents()
//            .stream()
//            .map(parentMapper::toSummaryDto)
//            .collect(Collectors.toList());
//}
//
//@Override
//public ChildDetailsResponseDto getChildDetails(String regId) {
//
//    String currentUser = roleVerifier.getCurrentUsername();
//    Parent parent = parentRepository.findByEmail(currentUser)
//            .orElseThrow(() -> new UnauthorizedAccessException("Parent session invalid"));
//
//    Student child = studentRepository.findByRegId(regId)
//            .orElseThrow(() -> new ResourceNotFoundException("Student not found with given registration id."));
//
//    if (!child.getParent().getId().equals(parent.getId())) {
//        throw new UnauthorizedAccessException("You can only view your own child's details");
//    }
//
//    AttendanceConfig config = attendanceConfigRepo.findTopByOrderByCreatedAtDesc();
//    String academicYear = config != null ? config.getAcademicYear() : "N/A";
//    float percentage = attendancePercent(child);
//
//    // Safely extract Class and Teacher details
//    String className = "Unassigned";
//    String teacherName = null;
//    String teacherEmail = null;
//    String teacherMobile = null;
//    String teacherDegree = null;
//
//    if (child.getAssignedClass() != null) {
//        className = child.getAssignedClass().getClassName();
//        if (child.getAssignedClass().getClassTeacher() != null) {
//            Teacher t = child.getAssignedClass().getClassTeacher();
//            teacherName = t.getFirstName() + " " + t.getLastName();
//            teacherEmail = t.getEmail();
//            teacherMobile = t.getMobile();
//            teacherDegree = t.getDegreeType() != null ? t.getDegreeType().name() : null;
//        }
//    }
//
//    return ChildDetailsResponseDto.builder()
//            .regId(child.getRegId())
//            .academicYear(academicYear)
//            .attendancePercentage(percentage)
//            .firstName(child.getFirstName())
//            .middleName(child.getMiddleName())
//            .lastName(child.getLastName())
//            .gender(child.getGender() != null ? child.getGender().name() : null)
//            .dob(child.getDob())
//            .bloodGroup(child.getBloodGroup() != null ? child.getBloodGroup().name() : null)
//            .aadharNo(child.getAadharNo())
//            .nationality(child.getNationality())
//            .religion(child.getReligion() != null ? child.getReligion().name() : null)
//            .category(child.getCategory() != null ? child.getCategory().name() : null)
//            .standard(child.getStandard())
//            .rollNumber(child.getRollNumber())
//            .className(className)
//            .classTeacherName(teacherName)
//            .classTeacherEmail(teacherEmail)
//            .classTeacherMobile(teacherMobile)
//            .classTeacherDegree(teacherDegree)
//            .previousSchool(child.getPreviousSchool())
//            .previousPercentage(child.getPreviousPercentage())
//            .birthCertificateUrl(child.getBirthCertificateUrl())
//            .aadharPhotoUrl(child.getAadharPhotoUrl())
//            .previousMarksheetUrl(child.getPreviousMarksheetUrl())
//            .passportPhotoUrl(child.getPassportPhotoUrl())
//            .leavingCertificateUrl(child.getLeavingCertificateUrl())
//            .casteCertificateUrl(child.getCasteCertificateUrl())
//            .incomeCertificateUrl(child.getIncomeCertificateUrl())
//            .build();
//}
//
//// ================= Helper Method ====================
//
//float attendancePercent(Student child) {
//
//    if (child.getAssignedClass() == null) return 0f;
//    if (child.getRollNumber() == null) return 0f;
//
//    AttendanceConfig config = attendanceConfigRepo.findTopByOrderByCreatedAtDesc();
//    if (config == null) return 0f;
//
//    String academicYear = config.getAcademicYear();
//    Long classId = child.getAssignedClass().getId();
//    Integer rollNumber = child.getRollNumber();
//
//    long sessionsConducted = studentAttendanceRepo.countTotalSessionsConducted(classId, academicYear);
//    if (sessionsConducted == 0) return 0f;
//
//    long totalAbsent = studentAttendanceRepo.countAbsentDays(classId, academicYear, rollNumber);
//    long sessionsConducted = studentAttendanceRepo.countTotalSessionsConducted(child.getAssignedClass().getId(), academicYear);
//
//    Integer rollStr = child.getRollNumber();
//    long totalAbsent  = studentAttendanceRepo.countAbsentDays(child.getAssignedClass().getId(), academicYear, rollStr);
//    long totalPresent = sessionsConducted - totalAbsent;
//
//    return (totalPresent * 100.0f) / sessionsConducted;
//}
//
//public Parent getParentById(Long id) {
//    return parentRepository.findById(id)
//            .orElseThrow(() -> new RuntimeException("Parent not found with id: " + id));
//}
//}


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
import com.example.school_management.parent.dto.ParentsDashboardResponseDto;
import com.example.school_management.parent.entity.Parent;
import com.example.school_management.parent.mapper.ParentMapper;
import com.example.school_management.parent.repository.ParentRepository;
import com.example.school_management.student.entity.Student;
import com.example.school_management.student.repository.StudentRepository;
import com.example.school_management.teacher.entity.Teacher;
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
    public ParentsDashboardResponseDto getDashboard(String registrationId) {
        String currentUser = roleVerifier.getCurrentUsername();
        Parent parent = parentRepository.findByEmail(currentUser)
                .orElseThrow(() -> new UnauthorizedAccessException("Parent session invalid"));

        // Fetch the specific child using the Registration ID
        Student child = studentRepository.findByRegId(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        // Security check
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

        // 🚀 FIXED: The builder syntax was completely destroyed in your version
        return ParentsDashboardResponseDto.builder()
                .studentId(child.getId()) // Assuming this field exists in your DTO
                .parentName(parent.getFatherFirstName() + " " + parent.getFatherLastName())
                .studentName(child.getFirstName() + " " + child.getLastName())
                .schoolClass(isClassAllocated ? child.getAssignedClass().getClassName() : "Unassigned")
                .isClassAllocated(isClassAllocated)
                .isProfileComplete(isProfileComplete)
                .attendancePercent(attendancePercent)
                .pendingAssignments(pendingAssignments)
                .recentNotifications(recentNotifications)
                .build();
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

        // Safely extract Class and Teacher details
        String className = "Unassigned";
        String teacherName = null;
        String teacherEmail = null;
        String teacherMobile = null;
        String teacherDegree = null;

        if (child.getAssignedClass() != null) {
            className = child.getAssignedClass().getClassName();
            if (child.getAssignedClass().getClassTeacher() != null) {
                Teacher t = child.getAssignedClass().getClassTeacher();
                teacherName = t.getFirstName() + " " + t.getLastName();
                teacherEmail = t.getEmail();
                teacherMobile = t.getMobile();
                teacherDegree = t.getDegreeType() != null ? t.getDegreeType().name() : null;
            }
        }

        return ChildDetailsResponseDto.builder()
                .regId(child.getRegId())
                .academicYear(academicYear)
                .attendancePercentage(percentage)
                .firstName(child.getFirstName())
                .middleName(child.getMiddleName())
                .lastName(child.getLastName())
                .gender(child.getGender() != null ? child.getGender().name() : null)
                .dob(child.getDob())
                .bloodGroup(child.getBloodGroup() != null ? child.getBloodGroup().name() : null)
                .aadharNo(child.getAadharNo())
                .nationality(child.getNationality())
                .religion(child.getReligion() != null ? child.getReligion().name() : null)
                .category(child.getCategory() != null ? child.getCategory().name() : null)
                .standard(child.getStandard())
                .rollNumber(child.getRollNumber())
                .className(className)
                .classTeacherName(teacherName)
                .classTeacherEmail(teacherEmail)
                .classTeacherMobile(teacherMobile)
                .classTeacherDegree(teacherDegree)
                .previousSchool(child.getPreviousSchool())
                .previousPercentage(child.getPreviousPercentage())
                .birthCertificateUrl(child.getBirthCertificateUrl())
                .aadharPhotoUrl(child.getAadharPhotoUrl())
                .previousMarksheetUrl(child.getPreviousMarksheetUrl())
                .passportPhotoUrl(child.getPassportPhotoUrl())
                .leavingCertificateUrl(child.getLeavingCertificateUrl())
                .casteCertificateUrl(child.getCasteCertificateUrl())
                .incomeCertificateUrl(child.getIncomeCertificateUrl())
                .build();
    }

    // ================= Helper Method ====================

    // 🚀 FIXED: Removed duplicate variable declarations
    float attendancePercent(Student child) {
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
}