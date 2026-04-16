package com.example.school_management.attendance.entity;

import com.example.school_management.common.BaseEntity;
import com.example.school_management.common.enums.attendance.AttendanceType;
import jakarta.persistence.*;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Builder
@Table(name = "attendance_configs",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_config_academic_year",
                columnNames = {"academicYear"}
        )
)
public class AttendanceConfig extends BaseEntity {

    @Column(nullable = false, length = 10)
    private String academicYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceType attendanceType = AttendanceType.DAY_WISE;

    @Column(nullable = false)
    private Float studentMinAttendancePct;

    @Column(nullable = false)
    private Float teacherMinAttendancePct;

    @Column(nullable = false)
    private Integer studentWorkingDays;

    @Column(nullable = false)
    private Integer teacherWorkingDays;

    private Boolean allowLateMarks = true;

    @Column(nullable = false)
    private Integer lateMarksWindow;

    @Column(nullable = false)
    private Boolean notifyParentOnAbsent = true;

    private Integer absentNotifyThreshold;

    private Integer minLeaveNoticeDays;
}