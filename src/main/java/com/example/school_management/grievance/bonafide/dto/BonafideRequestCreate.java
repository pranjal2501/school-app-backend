package com.example.school_management.grievance.bonafide.dto;
//Parent submits a request

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonafideRequestCreate {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @Size(max = 255, message = "Purpose must be under 255 characters")
    private String purpose;
}