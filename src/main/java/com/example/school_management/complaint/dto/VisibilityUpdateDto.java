package com.example.school_management.complaint.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VisibilityUpdateDto {
    @NotNull(message = "Visibility flag is required")
    private Boolean isVisibleToParents;
}