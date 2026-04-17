//package com.example.school_management.attendance.entity;
//
//import com.example.school_management.admin.entity.Admin;
//import com.example.school_management.common.BaseEntity;
//import com.example.school_management.common.enums.attendance.AttendanceStatus;
//import com.example.school_management.teacher.entity.Teacher;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//
//
//@Entity
//@AllArgsConstructor
//@NoArgsConstructor
//@Table(name = "Teacher_Attendance",
//        uniqueConstraints = {
//                @UniqueConstraint(
//                        name = "uk_teacher_date",
//                        columnNames = {"teacherId", "date"}
//                )
//        },
//        indexes = {
//                @Index(name = "idx_ta_year_date", columnList = "academicYear, date")
//        })
//public class TeacherAttendance extends BaseEntity {
//
//    private LocalDate date;
//
//    private String academicYear;
//
//    private AttendanceStatus status;
//
//    private LocalTime checkInTime;
//
//    private LocalTime checkOutTime;
//
//    private String remarks;
//
//    @ManyToOne
//    @JoinColumn(name = "teacherId", nullable = false)
//    private Teacher teacher;
//
//    @ManyToOne
//    @JoinColumn(name = "markedByAdmin", nullable = false)
//    private Admin markedBy;
//
//}
