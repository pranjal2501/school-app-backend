package com.example.school_management.grievance.lc.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LCCreateRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    private String reason;
}