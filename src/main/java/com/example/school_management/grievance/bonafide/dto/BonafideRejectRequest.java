package com.example.school_management.grievance.bonafide.dto;
//Admin rejects with a reason


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonafideRejectRequest {

    @NotBlank(message = "Rejection reason is required")
    @Size(max = 500, message = "Remarks must be under 500 characters")
    private String remarks;
}
