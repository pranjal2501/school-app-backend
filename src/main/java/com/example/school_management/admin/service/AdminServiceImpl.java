package com.example.school_management.admin.service;

import com.example.school_management.admin.dto.dashboard.AdminDashboardResponseDto;
import com.example.school_management.admin.dto.dashboard.DashboardStatsDto;
import com.example.school_management.admin.dto.dashboard.TodayClassDto;
import com.example.school_management.admin.mapper.AdminMapper;
import com.example.school_management.admin.dto.AdminRequestDto;
import com.example.school_management.admin.dto.AdminResponseDto;
import com.example.school_management.admin.entity.Admin;
import com.example.school_management.admin.repository.AdminRepository;
import com.example.school_management.attendance.entity.AttendanceConfig;
import com.example.school_management.attendance.entity.StudentAttendance;
import com.example.school_management.attendance.repository.AttendanceConfigRepo;
import com.example.school_management.attendance.repository.StudentAttendanceRepo;
import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.enums.Role;
import com.example.school_management.common.enums.notify.AudienceType;
import com.example.school_management.common.exception.DuplicateResourceException;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.common.exception.UnauthorizedAccessException;
import com.example.school_management.notify.dto.NotificationResponseDto;
import com.example.school_management.notify.mapper.NotificationMapper;
import com.example.school_management.notify.repository.NotificationRepository;
import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.schoolClass.repo.SchoolClassRepository;
import com.example.school_management.student.repository.StudentRepository;
import com.example.school_management.teacher.entity.Teacher;
import com.example.school_management.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Automatically generates a constructor for all 'final' fields
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final SchoolClassRepository classRepository;
    private final AttendanceConfigRepo attendanceConfigRepo;
    private final StudentAttendanceRepo studentAttendanceRepo;
    private final AdminMapper adminMapper;
    private final RoleVerifier roleVerifier;

    private void verifyAndGetAdminRecord() {
        if (!roleVerifier.hasRole(Role.ADMIN)) {
            throw new org.springframework.security.access.AccessDeniedException("Access Denied: Only Admins can perform this action.");
        }
        adminRepository.findByUsername(roleVerifier.getCurrentUsername())
                .orElseThrow(() -> new AccessDeniedException("Admin record not found."));
    }

    @Override
    @Transactional
    public AdminResponseDto createAdmin(AdminRequestDto request) {
//        verifyAndGetAdminRecord();

        Admin admin = adminMapper.toEntity(request);
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setActive(true);

        return adminMapper.toResponseDto(adminRepository.save(admin));
    }

    @Override
    public List<AdminResponseDto> getAllAdmins() {
        verifyAndGetAdminRecord();
        return adminMapper.toResponseDtoList(adminRepository.findAll());
    }

    @Override
    public AdminResponseDto getAdminById(Long id) {
        verifyAndGetAdminRecord();

        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "id", id));

        return adminMapper.toResponseDto(admin);
    }

    @Override
    public AdminDashboardResponseDto getDashboard() {
        // ===================== DATE RANGES =====================
        ZoneId zone = ZoneId.systemDefault();
        Instant now = Instant.now();
        Instant thisMonthStart = now.atZone(zone)
                .withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0)
                .toInstant();
        Instant lastMonthStart = thisMonthStart.atZone(zone)
                .minusMonths(1).toInstant();
        Instant lastMonthEnd = thisMonthStart.minusSeconds(1);

        // ===================== STATS =====================

        DashboardStatsDto stats = buildStats(thisMonthStart, lastMonthStart, lastMonthEnd, zone);

        // ===================== TODAY'S CLASSES =====================
        List<TodayClassDto> todayClasses = buildTodayClasses();

        // ===================== IMPORTANT ALERTS =====================
        List<NotificationResponseDto> alerts = notificationRepository
                .findTop4ByAudienceTypeOrderByCreatedAtDesc(AudienceType.ADMIN)
                .stream()
                .map(notification -> notificationMapper.toResponseDto(notification))
                .collect(Collectors.toList());

        return AdminDashboardResponseDto.builder()
                .stats(stats)
                .todayClasses(todayClasses)
                .importantAlerts(alerts)
                .build();
    }

    // ===================== STATS BUILDER =====================
    private DashboardStatsDto buildStats(
            Instant thisMonthStart,
            Instant lastMonthStart,
            Instant lastMonthEnd,
            ZoneId zone) {

        long totalStudents = studentRepository.countByIsActiveTrue();
        long studentsThisMonth = studentRepository
                .countByIsActiveTrueAndCreatedAtAfter(thisMonthStart);
        long studentsLastMonth = studentRepository
                .countByIsActiveTrueAndCreatedAtBetween(lastMonthStart, lastMonthEnd);
        double studentGrowth = calculateGrowth(studentsThisMonth, studentsLastMonth);

        long totalTeachers = teacherRepository.countByIsActiveTrue();
        long teachersThisMonth = teacherRepository
                .countByIsActiveTrueAndCreatedAtAfter(thisMonthStart);
        long teachersLastMonth = teacherRepository
                .countByIsActiveTrueAndCreatedAtBetween(lastMonthStart, lastMonthEnd);
        double teacherGrowth = calculateGrowth(teachersThisMonth, teachersLastMonth);

        long totalClasses = classRepository.count();
        long classesThisMonth = classRepository.countByCreatedAtAfter(thisMonthStart);
        long classesLastMonth = classRepository
                .countByCreatedAtBetween(lastMonthStart, lastMonthEnd);
        double classGrowth = calculateGrowth(classesThisMonth, classesLastMonth);
        // attendance
        AttendanceConfig config = attendanceConfigRepo
                .findTopByOrderByCreatedAtDesc();
        Float avgAttendance = 0f;
        Double attendanceGrowth = 0.0;

        if (config != null) {
            avgAttendance = calculateAvgAttendance(config.getAcademicYear());

            // convert Instant to LocalDate for attendance range query
            LocalDate lastMonthStartDate = lastMonthStart.atZone(zone).toLocalDate();
            LocalDate lastMonthEndDate = lastMonthEnd.atZone(zone).toLocalDate();

            Float lastMonthAvg = calculateAvgAttendanceForRange(
                    config.getAcademicYear(), lastMonthStartDate, lastMonthEndDate);

            if (lastMonthAvg != null && lastMonthAvg > 0) {
                attendanceGrowth = (double) ((avgAttendance - lastMonthAvg) / lastMonthAvg) * 100;
            }
        }

        return DashboardStatsDto.builder()
                .totalStudents(totalStudents)
                .studentGrowthPercent(studentGrowth)
                .totalTeachers(totalTeachers)
                .teacherGrowthPercent(teacherGrowth)
                .totalClasses(totalClasses)
                .classGrowthPercent(classGrowth)
                .avgAttendance(avgAttendance)
                .attendanceGrowthPercent(attendanceGrowth)
                .build();
    }

    // ===================== TODAY'S CLASSES =====================
    private List<TodayClassDto> buildTodayClasses() {
        List<SchoolClass> allClasses = classRepository.findAll();
        LocalDate today = LocalDate.now();
        List<TodayClassDto> result = new ArrayList<>();

        for (SchoolClass schoolClass : allClasses) {
            Optional<StudentAttendance> attendance = studentAttendanceRepo
                    .findByClassEntityAndAttendanceDate(schoolClass, today);

            int totalStudents = studentRepository
                    .countByAssignedClassId(schoolClass.getId());

            Integer totalPresent = null;
            boolean attendanceMarked = false;

            if (attendance.isPresent()) {
                int absentCount = attendance.get().getAbsentRollNumbers() != null
                        ? attendance.get().getAbsentRollNumbers().size() : 0;
                totalPresent = totalStudents - absentCount;
                attendanceMarked = true;
            }

            Teacher classTeacher = schoolClass.getClassTeacher();
            String teacherName = classTeacher != null
                    ? classTeacher.getFirstName() + " " + classTeacher.getLastName()
                    : "Not Assigned";

            result.add(TodayClassDto.builder()
                    .className(schoolClass.getClassName())
                    .classTeacherName(teacherName)
                    .totalStudents(totalStudents)
                    .totalPresent(totalPresent)
                    .attendanceMarked(attendanceMarked)
                    .build());
        }

        return result;

    }

    // ===================== HELPER METHODS =====================
    private double calculateGrowth(long current, long previous) {
        if (previous == 0) return current > 0 ? 100.0 : 0.0;
        return ((double)(current - previous) / previous) * 100;
    }

    private Float calculateAvgAttendance(String academicYear) {
        List<StudentAttendance> records = studentAttendanceRepo
                .findByAcademicYear(academicYear);
        if (records.isEmpty()) return 0f;

        int totalPresent = 0;
        int totalPossible = 0;

        for (StudentAttendance record : records) {
            int classStudents = studentRepository
                    .countByAssignedClassId(record.getClassEntity().getId());
            int absent = record.getAbsentRollNumbers() != null
                    ? record.getAbsentRollNumbers().size() : 0;
            totalPresent += (classStudents - absent);
            totalPossible += classStudents;
        }

        return totalPossible > 0 ? (totalPresent * 100.0f) / totalPossible : 0f;
    }

    private Float calculateAvgAttendanceForRange(
            String academicYear, LocalDate from, LocalDate to) {
        List<StudentAttendance> records = studentAttendanceRepo
                .findByAcademicYearAndAttendanceDateBetween(academicYear, from, to);
        if (records.isEmpty()) return null;

        int totalPresent = 0;
        int totalPossible = 0;

        for (StudentAttendance record : records) {
            int classStudents = studentRepository
                    .countByAssignedClassId(record.getClassEntity().getId());
            int absent = record.getAbsentRollNumbers() != null
                    ? record.getAbsentRollNumbers().size() : 0;
            totalPresent += (classStudents - absent);
            totalPossible += classStudents;
        }

        return totalPossible > 0 ? (totalPresent * 100.0f) / totalPossible : null;
    }
}