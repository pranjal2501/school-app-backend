package com.example.school_management.grievance.lc.dto;

import com.example.school_management.common.enums.GrievanceStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LCRequestResponse {

    private Long id;

    // Student info
    private Long studentId;
    private String studentName;
    private String studentRegId;
    private String studentClass;
    private String studentStandard;

    // Parent info
    private Long requestedById;
    private String requestedByName;

    // Request info
    private GrievanceStatus status;
    private String reason;
    private String remarks;

    // Approval fields
    private LocalDate leavingDate;
    private String conduct;
    private String lastAttendedYear;

    // Timestamps
    private LocalDateTime requestedAt;
    private LocalDateTime resolvedAt;
}