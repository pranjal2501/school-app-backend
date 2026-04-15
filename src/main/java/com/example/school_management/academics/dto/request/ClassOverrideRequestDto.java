package com.example.school_management.academics.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

// Payload for Class Overrides (Point 4)
@Data
public class ClassOverrideRequestDto {
    @NotNull
    private Long subjectId;
    @NotNull
    private Long newTeacherId;
}