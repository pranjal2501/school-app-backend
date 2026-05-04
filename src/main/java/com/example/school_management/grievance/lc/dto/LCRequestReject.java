package com.example.school_management.grievance.lc.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LCRequestReject {

    @NotBlank(message = "Remarks are required")
    private String remarks;
}