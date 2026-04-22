package com.example.school_management.attendance.mapper;

import com.example.school_management.attendance.dto.studentAttendance.*;
import com.example.school_management.attendance.entity.AttendanceConfig;
import com.example.school_management.attendance.entity.StudentAttendance;
import com.example.school_management.common.enums.Gender;
import com.example.school_management.common.enums.attendance.AttendanceStatus;
import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.student.entity.Student;
import com.example.school_management.subject.entity.Subject;
import com.example.school_management.teacher.entity.Teacher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StudentAttendanceMapper {

    public StudentAttendance toEntity(MarkAttendanceRequestDto request,
                                      SchoolClass classEntity,
                                      Teacher markedBy,
                                      Subject subject) {
        if (request == null) {
            return null;
        }

        return StudentAttendance.builder()
                .attendanceDate(request.getAttendanceDate())
                .academicYear(request.getAcademicYear())
                .classEntity(classEntity)
                .markedBy(markedBy)
                // The subject parameter is passed but not utilized in the original entity setup,
                // add it here if StudentAttendance has a subject field: .subject(subject)
                .absentRollNumbers(request.getAbsentRollNumbers() != null ? request.getAbsentRollNumbers() : new ArrayList<>())
                .lateRollNumbers(request.getLateRollNumbers() != null ? request.getLateRollNumbers() : new ArrayList<>())
                .halfDayRollNumbers(request.getHalfDayRollNumbers() != null ? request.getHalfDayRollNumbers() : new ArrayList<>())
                .build();
    }

    public MarkAttendanceResponseDto toMarkResponse(StudentAttendance saved, int totalStudents) {
        if (saved == null) {
            return null;
        }

        List<Integer> absent = saved.getAbsentRollNumbers() != null ? saved.getAbsentRollNumbers() : new ArrayList<>();
        List<Integer> late = saved.getLateRollNumbers() != null ? saved.getLateRollNumbers() : new ArrayList<>();
        List<Integer> halfDay = saved.getHalfDayRollNumbers() != null ? saved.getHalfDayRollNumbers() : new ArrayList<>();

        // Safely extract nested entity data
        Long classId = (saved.getClassEntity() != null) ? saved.getClassEntity().getId() : null;
        String className = (saved.getClassEntity() != null) ? saved.getClassEntity().getClassName() : null;

        Long markedById = null;
        String markedByName = null;
        if (saved.getMarkedBy() != null) {
            markedById = saved.getMarkedBy().getId();
            markedByName = saved.getMarkedBy().getFirstName() + " " + saved.getMarkedBy().getLastName();
        }

        return MarkAttendanceResponseDto.builder()
                .id(saved.getId())
                .attendanceDate(saved.getAttendanceDate())
                .academicYear(saved.getAcademicYear())
                .classId(classId)
                .className(className)
                .markedById(markedById)
                .markedByName(markedByName)
                .absentRollNumbers(absent)
                .lateRollNumbers(late)
                .halfDayRollNumbers(halfDay)
                .totalStudents(totalStudents)
                .totalAbsent(absent.size())
                .totalLate(late.size())
                .totalHalfDay(halfDay.size())
                .totalPresent(totalStudents - absent.size() - late.size() - halfDay.size())
                .build();
    }



    public StudentAttendanceReportResponseDto toReportResponse(
            Student student,
            SchoolClass classEntity,
            AttendanceConfig config,
            long totalAbsent,
            long totalLate,
            long totalHalfDay,
            float percentage,
            Long totalPresent,
            Long sessionsConducted) {

        if (student == null || classEntity == null || config == null) {
            return null;
        }

        return StudentAttendanceReportResponseDto.builder()
                .studentName(student.getFirstName() + " " + student.getLastName())
                .rollNumber(student.getRollNumber())
                .className(classEntity.getClassName())
                .academicYear(config.getAcademicYear())
                .totalWorkingDays(config.getStudentWorkingDays())
                .daysConducted(sessionsConducted)
                .totalAbsent(totalAbsent)
                .totalLate(totalLate)
                .totalHalfDay(totalHalfDay)
                .totalPresent(totalPresent)
                .attendancePercentage(percentage)
                .isShortage(percentage < config.getStudentMinAttendancePct())
                .build();
    }

    public StudentMonthlyCalendarResponseDto toCalendarResponse(
            Student student,
            SchoolClass classEntity,
            LocalDate monthStart,
            List<StudentMonthlyCalendarResponseDto.DailyRecord> dailyRecords,
            int totalPresent,
            int totalAbsent,
            int totalHolidays,
            float percentage) {

        if (student == null || classEntity == null || monthStart == null) {
            return null;
        }

        return StudentMonthlyCalendarResponseDto.builder()
                .studentName(student.getFirstName() + " " + student.getLastName())
                .rollNumber(student.getRollNumber())
                .className(classEntity.getClassName())
                .month(monthStart.getMonth().name() + " " + monthStart.getYear())
                .totalPresent(totalPresent)
                .totalAbsent(totalAbsent)
                .totalHolidays(totalHolidays)
                .attendancePercentage(percentage)
                .dailyRecords(dailyRecords)
                .build();
    }

    public StudentAttendanceRecordDto toRecordDto(Student student, AttendanceStatus status, String className) {
        return StudentAttendanceRecordDto.builder()
                .studentId(student.getId())
                .studentName(student.getFirstName() + " " + student.getLastName())
                .rollNumber(String.valueOf(student.getRollNumber()))
                .className(className)
                .gender(student.getGender())
                .status(status)
                .build();
    }
    public SingleDayAttendanceResponseDto toSingleDayResponseDto(
            LocalDate date,
            List<StudentAttendanceRecordDto> records) {

        long totalPresent = records.stream()
                .filter(r -> r.getStatus() == AttendanceStatus.PRESENT).count();
        long totalAbsent = records.stream()
                .filter(r -> r.getStatus() == AttendanceStatus.ABSENT).count();
        long totalLate = records.stream()
                .filter(r -> r.getStatus() == AttendanceStatus.LATE).count();
        long totalBoys = records.stream()
                .filter(r -> r.getGender() == Gender.MALE).count();
        long totalGirls = records.stream()
                .filter(r -> r.getGender() == Gender.FEMALE).count();
        long totalBoysPresent = records.stream()
                .filter(r -> r.getGender() == Gender.MALE
                        && r.getStatus() == AttendanceStatus.PRESENT).count();
        long totalGirlsPresent = records.stream()
                .filter(r -> r.getGender() == Gender.FEMALE
                        && r.getStatus() == AttendanceStatus.PRESENT).count();

        return SingleDayAttendanceResponseDto.builder()
                .date(date)
                .totalStudents(records.size())
                .totalPresent((int) totalPresent)
                .totalAbsent((int) totalAbsent)
                .totalLate((int) totalLate)
                .totalBoys((int) totalBoys)
                .totalGirls((int) totalGirls)
                .totalBoysPresent((int) totalBoysPresent)
                .totalGirlsPresent((int) totalGirlsPresent)
                .records(records)
                .build();
    }
}