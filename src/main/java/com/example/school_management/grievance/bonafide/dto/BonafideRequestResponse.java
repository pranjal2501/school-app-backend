package com.example.school_management.grievance.bonafide.dto;


import com.example.school_management.common.enums.BonafideStatus;
import lombok.*;

import java.time.LocalDateTime;
//What parent/admin sees for a request

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonafideRequestResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private String studentClass;
    private Long requestedById;
    private String requestedByName;
    private BonafideStatus status;
    private String purpose;
    private String remarks;
    private LocalDateTime requestedAt;
    private LocalDateTime resolvedAt;
}
