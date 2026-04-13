package com.example.school_management.attendance.entity;

import com.example.school_management.common.BaseEntity;
import com.example.school_management.common.enums.Attendence.AttendanceType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Attendance_Configurations",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_config_academic_year", columnNames = {"academicYear"})
        })
public class AttendanceConfig extends BaseEntity {

    private String academicYear;

    private AttendanceType attendanceType;

    private Float minAttendance;

    private Integer workingDays;

    private Boolean allowLateMarks;

    private Integer lateMarksWindow;

    private Boolean notifyParentOnAbsent;

    private Integer absentNotifyThreshold;

}
