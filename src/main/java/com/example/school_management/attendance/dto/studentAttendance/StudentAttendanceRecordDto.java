package com.example.school_management.attendance.dto.studentAttendance;

import com.example.school_management.common.enums.Gender;
import com.example.school_management.common.enums.attendance.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAttendanceRecordDto {
    private Long studentId;
    private String studentName;
    private String rollNumber;
    private String className;
    private Gender gender;
    private AttendanceStatus status;
    private String remarks;
}
