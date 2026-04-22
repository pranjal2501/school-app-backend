package com.example.school_management.complaint.dto;

import com.example.school_management.common.ValidationMessages;
import com.example.school_management.common.enums.ComplaintCategory;
import com.example.school_management.common.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class ComplaintRequestDto {

    @NotEmpty(message = "At least one student must be selected.")
    private List<Long> targetStudentIds;

    @NotBlank(message = ValidationMessages.TITLE_REQUIRED)
    @Size(max = 150, message = ValidationMessages.TITLE_MAX_LENGTH)
    private String title;

    @NotBlank(message = ValidationMessages.DESCRIPTION_REQUIRED)
    @Size(max = 1000, message = ValidationMessages.DESCRIPTION_MAX_LENGTH)
    private String description;

    private Boolean isVisibleToParents = true;

    @NotNull(message = ValidationMessages.CATEGORY_REQUIRED)
    private ComplaintCategory category;

    @NotNull(message = ValidationMessages.PRIORITY_REQUIRED)
    private Priority priority;

    private List<String> supportedMediaUrls;
}