package com.example.school_management.academics.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ClassBulkSetupRequestDto {

    @NotBlank(message = "Standard cannot be empty")
    private String standard; // e.g., "6"

    @NotEmpty(message = "At least one division must be provided")
    private List<String> divisions; // e.g., ["A", "B", "C"]
}