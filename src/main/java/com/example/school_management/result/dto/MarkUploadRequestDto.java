package com.example.school_management.result.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class MarkUploadRequestDto {
    @NotNull(message = "Allocation ID is required.")
    private Long allocationId;

    @NotNull(message = "Exam Type ID is required.")
    private Long examTypeId;

    @NotEmpty(message = "You must provide marks for at least one student.")
    @Valid
    private List<StudentMarkEntryDto> marks;
}