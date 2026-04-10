package com.example.school_management.complaint.dto;

import com.example.school_management.common.ValidationMessages;
import com.example.school_management.common.enums.ComplaintStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ComplaintStatusUpdateDto {

    @NotNull(message = ValidationMessages.STATUS_REQUIRED)
    private ComplaintStatus newStatus;

    @Size(max = 1000, message = ValidationMessages.RESOLUTION_COMMENT_MAX_LENGTH)
    private String resolutionComment;
}