package com.example.school_management.attendance.mapper;

import com.example.school_management.attendance.dto.studentAttendance.MarkAttendanceRequestDto;
import com.example.school_management.attendance.dto.studentAttendance.MarkAttendanceResponseDto;
import com.example.school_management.attendance.dto.studentAttendance.StudentAttendanceReportResponseDto;
import com.example.school_management.attendance.dto.studentAttendance.StudentMonthlyCalendarResponseDto;
import com.example.school_management.attendance.entity.AttendanceConfig;
import com.example.school_management.attendance.entity.StudentAttendance;
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

        StudentAttendance attendance = new StudentAttendance();

        attendance.setAttendanceDate(request.getAttendanceDate());
        attendance.setAcademicYear(request.getAcademicYear());
        attendance.setClassEntity(classEntity);
        attendance.setMarkedBy(markedBy);

        attendance.setAbsentRollNumbers(request.getAbsentRollNumbers() != null
                ? request.getAbsentRollNumbers() : new ArrayList<>());
        attendance.setLateRollNumbers(request.getLateRollNumbers() != null
                ? request.getLateRollNumbers() : new ArrayList<>());
        attendance.setHalfDayRollNumbers(request.getHalfDayRollNumbers() != null
                ? request.getHalfDayRollNumbers() : new ArrayList<>());

        return attendance;
    }

    public MarkAttendanceResponseDto toMarkResponse(StudentAttendance saved, int totalStudents) {
        MarkAttendanceResponseDto res = new MarkAttendanceResponseDto();

        res.setId(saved.getId());
        res.setAttendanceDate(saved.getAttendanceDate());
        res.setAcademicYear(saved.getAcademicYear());
        res.setClassId(saved.getClassEntity().getId());
        res.setClassName(saved.getClassEntity().getClassName());
        res.setMarkedById(saved.getMarkedBy().getId());
        res.setMarkedByName(saved.getMarkedBy().getFirstName() + " " + saved.getMarkedBy().getLastName());

        List<Integer> absent = saved.getAbsentRollNumbers();
        List<Integer> late = saved.getLateRollNumbers();
        List<Integer> halfDay = saved.getHalfDayRollNumbers();

        res.setAbsentRollNumbers(absent);
        res.setLateRollNumbers(late);
        res.setHalfDayRollNumbers(halfDay);

        res.setTotalStudents(totalStudents);
        res.setTotalAbsent(absent.size());
        res.setTotalLate(late.size());
        res.setTotalHalfDay(halfDay.size());
        res.setTotalPresent(totalStudents - absent.size() - late.size() - halfDay.size());

        return res;
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

        StudentAttendanceReportResponseDto response = new StudentAttendanceReportResponseDto();

        response.setStudentName(student.getFirstName() + " " + student.getLastName());
        response.setRollNumber(student.getRollNumber());
        response.setClassName(classEntity.getClassName());
        response.setAcademicYear(config.getAcademicYear());

        response.setTotalWorkingDays(config.getStudentWorkingDays());
        response.setDaysConducted(sessionsConducted);

        response.setTotalAbsent(totalAbsent);
        response.setTotalLate(totalLate);
        response.setTotalHalfDay(totalHalfDay);

        response.setTotalPresent(totalPresent);

        response.setAttendancePercentage(percentage);
        response.setIsShortage(percentage < config.getStudentMinAttendancePct());

        return response;
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

        StudentMonthlyCalendarResponseDto res = new StudentMonthlyCalendarResponseDto();
        res.setStudentName(student.getFirstName() + " " + student.getLastName());
        res.setRollNumber(student.getRollNumber());
        res.setClassName(classEntity.getClassName());

        res.setMonth(monthStart.getMonth().name() + " " + monthStart.getYear());

        res.setTotalPresent(totalPresent);
        res.setTotalAbsent(totalAbsent);
        res.setTotalHolidays(totalHolidays);
        res.setAttendancePercentage(percentage);
        res.setDailyRecords(dailyRecords);

        return res;
    }
}