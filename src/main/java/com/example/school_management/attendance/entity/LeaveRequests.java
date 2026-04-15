package com.example.school_management.attendance.entity;

import com.example.school_management.admin.entity.Admin;
import com.example.school_management.common.BaseEntity;
import com.example.school_management.common.enums.Attendance.LeaveRequesterType;
import com.example.school_management.common.enums.Attendance.LeaveStatus;
import com.example.school_management.common.enums.Attendance.LeaveType;
import com.example.school_management.student.entity.Student;
import com.example.school_management.teacher.entity.Teacher;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "leave_requests", indexes = {
        @Index(name = "idx_leave_status", columnList = "status"),
        @Index(name = "idx_leave_from_date", columnList = "fromDate"),
        @Index(name = "idx_leave_academic_year_status", columnList = "academicYear, status"),
        @Index(name = "idx_leave_academic_year", columnList = "academicYear")
})
public class LeaveRequests extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveRequesterType requesterType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveType leaveType;

    @Column(nullable = false)
    private LocalDate fromDate;

    @Column(nullable = false)
    private LocalDate toDate;

    @Column(nullable = false)
    private Integer totalDays;

    @Column(nullable = false, length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveStatus status = LeaveStatus.PENDING; // Usually default to pending

    private String rejectionReason;

    private String supportingDoc; // URL or File Path

    private LocalDateTime approvedAt;

    @Column(nullable = false)
    private String academicYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private Admin approvedBy;

    // Helper method to ensure data integrity before saving
    @PrePersist
    @PreUpdate
    public void validateAndCalculate() {
        if (toDate.isBefore(fromDate)) {
            throw new IllegalArgumentException("To Date cannot be before From Date");
        }

        this.totalDays = (int) ChronoUnit.DAYS.between(fromDate, toDate) + 1;
    }
}