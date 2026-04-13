package com.example.school_management.attendance.entity;

import com.example.school_management.admin.entity.Admin;
import com.example.school_management.common.BaseEntity;
import com.example.school_management.common.enums.Attendence.LeaveRequesterType;
import com.example.school_management.common.enums.Attendence.LeaveStatus;
import com.example.school_management.common.enums.Attendence.LeaveType;
import com.example.school_management.student.entity.Student;
import com.example.school_management.teacher.entity.Teacher;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Leave_Requests")
public class LeaveRequests extends BaseEntity {

    private LeaveRequesterType requesterType;

    private LeaveType leaveType;

    private LocalDate fromDate;

    private LocalDate toDate;

    private Integer totalDays;

    private String reason;

    private LeaveStatus status=LeaveStatus.APPROVED;

    private String rejectionReason;

    private String supportingDoc;

    private LocalDateTime approvedAt;

    private String academicYear;

    @ManyToOne
    @JoinColumn(name = "studentId", nullable = true)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "teacherId", nullable = true)
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "approvedBy", nullable = true)
    private Admin approvedBy;
}
