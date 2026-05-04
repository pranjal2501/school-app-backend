package com.example.school_management.grievance.lc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LCRequestApprove {

    @NotNull(message = "Leaving date is required")
    private LocalDate leavingDate;

    @NotBlank(message = "Conduct is required")
    private String conduct;

    @NotBlank(message = "Last attended year is required")
    private String lastAttendedYear;
}