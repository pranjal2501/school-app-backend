package com.example.school_management.attendance.service;

import com.example.school_management.attendance.dto.ClassWeeklyStatDto;
import com.example.school_management.attendance.dto.studentAttendance.*;
import com.example.school_management.attendance.entity.AttendanceConfig;
import com.example.school_management.attendance.entity.SchoolHolidays;
import com.example.school_management.attendance.entity.StudentAttendance;
import com.example.school_management.attendance.mapper.StudentAttendanceMapper;
import com.example.school_management.attendance.repository.AttendanceConfigRepo;
import com.example.school_management.attendance.repository.SchoolHolidaysRepository;
import com.example.school_management.attendance.repository.StudentAttendanceRepo;
import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.enums.Role;
import com.example.school_management.common.enums.attendance.AttendanceStatus;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.notify.service.NotificationService;
import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.schoolClass.repo.SchoolClassRepository;
import com.example.school_management.student.entity.Student;
import com.example.school_management.student.repository.StudentRepository;
import com.example.school_management.teacher.entity.Teacher;
import com.example.school_management.teacher.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class StudentAttendanceService {

    private final StudentAttendanceRepo attendanceRepo;
    private final SchoolHolidaysRepository holidayRepo;
    private final SchoolClassRepository classRepo;
    private final TeacherRepository teacherRepo;
    private final StudentRepository studentRepo;
    private final AttendanceConfigRepo configRepo;
    private final StudentAttendanceMapper attendanceMapper;
    private final NotificationService notificationService;
    private final RoleVerifier verify;

    // ── Mark attendance for entire class ────────────────────────────

    @Transactional
    public MarkAttendanceResponseDto markAttendance(
            MarkAttendanceRequestDto request, Long markedByTeacherId) throws AccessDeniedException {

        if(!verify.hasRole(Role.TEACHER)){
            throw new AccessDeniedException("Only ADMIN can perform this action");
        }

        // 1. Guards
        if (request.getAttendanceDate().isAfter(LocalDate.now())) {
            throw new IllegalStateException("Cannot mark attendance for a future date");
        }
        if (request.getAttendanceDate().getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new IllegalStateException("Cannot mark attendance on Sunday");
        }
        if (holidayRepo.existsByHolidayDate(request.getAttendanceDate())) {
            throw new IllegalStateException("Cannot mark attendance on a holiday");
        }
        if (attendanceRepo.existsByClassEntityIdAndAttendanceDate(
                request.getClassId(), request.getAttendanceDate())) {
            throw new IllegalStateException("Attendance already marked for this class on " + request.getAttendanceDate());
        }

        // 2. Fetch dependencies
        SchoolClass classEntity = classRepo.findById(request.getClassId())
                .orElseThrow(() -> new EntityNotFoundException("Class not found"));

        Teacher markedBy = teacherRepo.findById(markedByTeacherId)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found"));

        // 3. Validate Roll Numbers (Fixing the Array/Stream error)
        // We use Arrays.asList() in case your repo returns an Array Student[]
        List<Student> studentList = studentRepo.findByAssignedClass_Id(request.getClassId());

        List<Integer> validRollNumbers = studentList.stream()
                .map(Student::getRollNumber)
                .collect(Collectors.toList());

        List<Integer> allSubmitted = new ArrayList<>();
        if (request.getAbsentRollNumbers() != null) allSubmitted.addAll(request.getAbsentRollNumbers());
        if (request.getLateRollNumbers() != null) allSubmitted.addAll(request.getLateRollNumbers());
        if (request.getHalfDayRollNumbers() != null) allSubmitted.addAll(request.getHalfDayRollNumbers());

        List<Integer> invalidRolls = allSubmitted.stream()
                .filter(roll -> !validRollNumbers.contains(roll))
                .collect(Collectors.toList());

        if (!invalidRolls.isEmpty()) {
            throw new IllegalStateException("Invalid roll numbers: " + invalidRolls);
        }

        // 4. Check for duplicates across status lists
        if (allSubmitted.size() != allSubmitted.stream().distinct().count()) {
            throw new IllegalStateException("A roll number cannot have multiple statuses simultaneously");
        }

        // 5. Map to Entity and Save (Cleaned up via Mapper)
        StudentAttendance attendance = attendanceMapper.toEntity(request, classEntity, markedBy, null);
        StudentAttendance saved = attendanceRepo.save(attendance);

//        if (!request.getAbsentRollNumbers().isEmpty() || !request.getLateRollNumbers().isEmpty()) {
//            notificationService.notifyParentsForUnattendedClass(
//                    request.getClassId(),
//                    request.getAttendanceDate(),
//                    request.getAbsentRollNumbers(),
//                    request.getLateRollNumbers(),
//                    request.getHalfDayRollNumbers()
//            );
//        }

        // 6. Return response via Mapper
        return attendanceMapper.toMarkResponse(saved, validRollNumbers.size());
    }

    /**
     * Helper method to bridge the saved entity and your existing mapper.
     */
    private MarkAttendanceResponseDto mapToResponseDto(StudentAttendance savedAttendance) {
        // 1. Get the total number of active students for this class
        int totalStudents = studentRepo
                .findAllByAssignedClassIdAndIsActiveTrue(savedAttendance.getClassEntity().getId())
                .size();

        // 2. Pass both the entity and the count to your existing mapper method
        return attendanceMapper.toMarkResponse(savedAttendance, totalStudents);
    }

    // ── Build monthly calendar — parent dashboard (single child) ────────────────────

    public StudentMonthlyCalendarResponseDto getMonthlyCalendar(StudentAttendanceCalendarRequestDto request) throws AccessDeniedException {

        if(!verify.hasAnyRole(Role.ADMIN, Role.TEACHER, Role.PARENT)){
            throw new AccessDeniedException("Only ADMIN can perform this action");
        }

        LocalDate from = LocalDate.of(request.getYear(), request.getMonth(), 1);
        LocalDate to = from.withDayOfMonth(from.lengthOfMonth());

        List<StudentAttendance> attendanceRows = attendanceRepo.findByClassEntityIdAndAttendanceDateBetween(request.getClassId(), from, to);

        Set<LocalDate> holidayDates = holidayRepo.findByHolidayDateBetween(from, to).stream()
                .map(SchoolHolidays::getHolidayDate) // Using method reference for cleaner code
                .collect(Collectors.toSet());

        Map<LocalDate, StudentAttendance> attendanceMap = attendanceRows.stream()
                .collect(Collectors.toMap(StudentAttendance::getAttendanceDate, a -> a));

        Student student = studentRepo.findByAssignedClass_IdAndRollNumber(request.getClassId(), request.getRollNumber())
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        SchoolClass classEntity = classRepo.findById(request.getClassId())
                .orElseThrow(() -> new EntityNotFoundException("Class not found"));

        List<StudentMonthlyCalendarResponseDto.DailyRecord> dailyRecords = new ArrayList<>();
        int totalPresent = 0, totalAbsent = 0, totalHolidays = 0, totalLate = 0, totalHalfDay = 0;

        LocalDate cursor = from;
        while (!cursor.isAfter(to)) {
            StudentMonthlyCalendarResponseDto.DailyRecord record = new StudentMonthlyCalendarResponseDto.DailyRecord();
            record.setDate(cursor);
            record.setDayOfMonth(cursor.getDayOfMonth());
            record.setDayOfWeek(cursor.getDayOfWeek().name().substring(0, 3));

            StudentMonthlyCalendarResponseDto.DailyStatus status;

            if (cursor.isAfter(LocalDate.now())) {
                status = StudentMonthlyCalendarResponseDto.DailyStatus.FUTURE;
            } else if (cursor.getDayOfWeek() == DayOfWeek.SUNDAY) {
                status = StudentMonthlyCalendarResponseDto.DailyStatus.SUNDAY;
            } else if (holidayDates.contains(cursor)) {
                status = StudentMonthlyCalendarResponseDto.DailyStatus.HOLIDAY;
                totalHolidays++;
            } else {
                StudentAttendance row = attendanceMap.get(cursor);
                if (row == null) {
                    status = StudentMonthlyCalendarResponseDto.DailyStatus.FUTURE;
                } else {
                    if (row.getAbsentRollNumbers().contains(request.getRollNumber())) {
                        status = StudentMonthlyCalendarResponseDto.DailyStatus.ABSENT;
                        totalAbsent++;
                    } else if (row.getLateRollNumbers().contains(request.getRollNumber())) {
                        status = StudentMonthlyCalendarResponseDto.DailyStatus.LATE;
                        totalLate++;
                        totalPresent++;
                    } else if (row.getHalfDayRollNumbers().contains(request.getRollNumber())) {
                        status = StudentMonthlyCalendarResponseDto.DailyStatus.HALF_DAY;
                        totalHalfDay++;
                        totalPresent++;
                    } else {
                        status = StudentMonthlyCalendarResponseDto.DailyStatus.PRESENT;
                        totalPresent++;
                    }
                }
            }

            // ── CRITICAL FIX 1: Add the record and increment the cursor ──
            record.setStatus(status);
            dailyRecords.add(record);
            cursor = cursor.plusDays(1);
        } // ── END WHILE LOOP ──

        // ── CRITICAL FIX 2: Calculation and Return outside the loop ──
        int totalMarkedDays = totalPresent + totalAbsent;
        float percentage = totalMarkedDays > 0
                ? (totalPresent * 100.0f) / totalMarkedDays
                : 0f;

        return attendanceMapper.toCalendarResponse(
                student,
                classEntity,
                from,
                dailyRecords,
                totalPresent,
                totalAbsent,
                totalHolidays,
                percentage
        );
    }
    // ── Yearly report (of single child) ────────────────────────────────────────────────

    public StudentAttendanceReportResponseDto getYearlyReport(StudentAttendanceReportRequestDto request) throws AccessDeniedException {

        if(!verify.hasAnyRole(Role.ADMIN, Role.PARENT, Role.TEACHER)){
            throw new AccessDeniedException("Only ADMIN can perform this action");
        }

        // 1. Fetch dependencies (Student, Class, Config)
        Student student = studentRepo.findByAssignedClass_IdAndRollNumber(request.getClassId(), request.getRollNumber())
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        SchoolClass classEntity = classRepo.findById(request.getClassId())
                .orElseThrow(() -> new EntityNotFoundException("Class not found"));

        AttendanceConfig config = configRepo.findByAcademicYear(request.getAcademicYear())
                .orElseThrow(() -> new EntityNotFoundException("Config not found"));

        // 2. NEW: Count how many times attendance was actually marked for this class
        long sessionsConducted = attendanceRepo.countTotalSessionsConducted(request.getClassId(), request.getAcademicYear());

        // 3. Query counts from DB
        Integer rollStr = request.getRollNumber();
        long totalAbsent  = attendanceRepo.countAbsentDays(request.getClassId(), request.getAcademicYear(), rollStr);
        long totalLate    = attendanceRepo.countLateDays(request.getClassId(), request.getAcademicYear(), rollStr);
        long totalHalfDay = attendanceRepo.countHalfDays(request.getClassId(), request.getAcademicYear(), rollStr);

        // 4. THE FIX: Calculation Logic
        // totalPresent is now based on REAL sessions, not the yearly goal
        long totalPresent = sessionsConducted - totalAbsent;

        float percentage = 0f;
        // Percentage is usually calculated against conducted days for mid-year reports
        if (sessionsConducted > 0) {
            percentage = (totalPresent * 100.0f) / sessionsConducted;
        }

        // Pass sessionsConducted to your mapper if you want to show "Actual Days" in the DTO
        return attendanceMapper.toReportResponse(
                student, classEntity, config, totalAbsent, totalLate, totalHalfDay, percentage, totalPresent, sessionsConducted
        );
    }

    // service to get today's or any specific date attendance
    @Transactional
    public MarkAttendanceResponseDto getClassAttendanceByDate(Long classId, LocalDate date) {
        return attendanceRepo.findByClassEntityIdAndAttendanceDate(classId, date)
                .map(attendance -> MarkAttendanceResponseDto.builder()
                        .id(attendance.getId())
                        .attendanceDate(attendance.getAttendanceDate())
                        .academicYear(attendance.getAcademicYear())
                        .classId(attendance.getClassEntity().getId())
                        .className(attendance.getClassEntity().getClassName())
                        .absentRollNumbers(attendance.getAbsentRollNumbers())
                        .lateRollNumbers(attendance.getLateRollNumbers())
                        .halfDayRollNumbers(attendance.getHalfDayRollNumbers())
                        .build())
                .orElse(null); // Return null so frontend knows no attendance was marked yet
    }

    @Transactional
    public MarkAttendanceResponseDto updateAttendance(MarkAttendanceRequestDto request, Long teacherId) {
        // 1. Fetch the existing record
        StudentAttendance existingAttendance = attendanceRepo.findByClassEntityIdAndAttendanceDate(request.getClassId(), request.getAttendanceDate())
                .orElseThrow(() -> new ResourceNotFoundException("Attendance", "date", request.getAttendanceDate()));

        // 2. Safely overwrite the arrays
        existingAttendance.setAbsentRollNumbers(request.getAbsentRollNumbers());
        existingAttendance.setLateRollNumbers(request.getLateRollNumbers());
        existingAttendance.setHalfDayRollNumbers(request.getHalfDayRollNumbers());

        // Optional: Update the "markedBy" if another teacher modifies it,
        // or just leave it as the original teacher.

        // 3. Save the updated entity
        StudentAttendance updated = attendanceRepo.save(existingAttendance);

        // Map back to response DTO (Use your existing mapping logic here)
        return mapToResponseDto(updated);
    }

    @Transactional
    public List<ClassWeeklyStatDto> getWeeklyClassStats(Long classId) {
        // Get the total active students in the class
        int totalStudents = studentRepo.findAllByAssignedClassIdAndIsActiveTrue(classId).size();

        if (totalStudents == 0) return new ArrayList<>();

        // Get the last 7 marked attendances
        List<StudentAttendance> recentAttendances = attendanceRepo
                .findTop7ByClassEntityIdAndAttendanceDateLessThanEqualOrderByAttendanceDateDesc(classId, LocalDate.now());

        return recentAttendances.stream().map(att -> {
            int absent = att.getAbsentRollNumbers() != null ? att.getAbsentRollNumbers().size() : 0;
            int late = att.getLateRollNumbers() != null ? att.getLateRollNumbers().size() : 0;
            int half = att.getHalfDayRollNumbers() != null ? att.getHalfDayRollNumbers().size() : 0;
            int present = totalStudents - absent - late - half;

            return ClassWeeklyStatDto.builder()
                    .date(att.getAttendanceDate())
                    .dayOfWeek(att.getAttendanceDate().getDayOfWeek().name())
                    .totalStudents(totalStudents)
                    .presentCount(present)
                    .absentCount(absent)
                    .lateCount(late)
                    .halfDayCount(half)
                    .presentPercentage(Math.round(((double) present / totalStudents) * 100.0))
                    .build();
        }).collect(Collectors.toList());
    }

    public SingleDayAttendanceResponseDto getSingleDayReport(SingleDayAttendanceRequestDto request) throws AccessDeniedException {

        if(!verify.hasAnyRole(Role.ADMIN, Role.TEACHER)){
            throw new AccessDeniedException("Only ADMIN and teacher can perform this action");
        }

        SchoolClass schoolClass = classRepo.findById(request.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Class", "id", request.getClassId()));

        StudentAttendance attendance  = attendanceRepo
                .findByClassEntityAndAttendanceDate(schoolClass, request.getDate())
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found for this class on " + request.getDate()));

        List<Student> students = studentRepo.findByAssignedClass_Id(request.getClassId());
        List<StudentAttendanceRecordDto> recordDtos = new ArrayList<>();

        for (Student student : students) {
            int roll = student.getRollNumber();

            AttendanceStatus status;
            if (attendance.getAbsentRollNumbers().contains(roll)) {
                status = AttendanceStatus.ABSENT;
            } else if (attendance.getLateRollNumbers().contains(roll)) {
                status = AttendanceStatus.LATE;
            } else if (attendance.getHalfDayRollNumbers().contains(roll)) {
                status = AttendanceStatus.HALF_DAY;
            } else {
                status = AttendanceStatus.PRESENT;
            }

            recordDtos.add(attendanceMapper.toRecordDto(student, status, schoolClass.getClassName()));
        }

        return attendanceMapper.toSingleDayResponseDto(request.getDate(), recordDtos);

    }
}
