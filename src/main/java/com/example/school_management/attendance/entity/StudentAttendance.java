package com.example.school_management.attendance.entity;

import com.example.school_management.common.BaseEntity;
import com.example.school_management.common.enums.Attendence.AttendanceStatus;
import com.example.school_management.common.enums.Attendence.AttendanceType;
import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.student.entity.Student;
import com.example.school_management.subject.entity.Subject;
import com.example.school_management.teacher.entity.Teacher;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Student_Attendance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_date_subject",
                        columnNames = {"studentId", "date", "subjectId"}
                )
        },
        indexes = {
                @Index(name = "idx_sa_class_date", columnList = "classId, date"),
                @Index(name = "idx_sa_year_date", columnList = "academicYear, date")
        })
public class StudentAttendance extends BaseEntity {
    private LocalDate date;
    private String academicYear;
    private AttendanceType attendanceType;
    private AttendanceStatus status;
    private String remarks;

    @ManyToOne
    @JoinColumn(name = "studentId", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "classId", nullable = false)
    private SchoolClass classId;

    @ManyToOne
    @JoinColumn(name = "subjectId", nullable = true)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "markedBy", nullable = false)
    private Teacher markedBy;
}
