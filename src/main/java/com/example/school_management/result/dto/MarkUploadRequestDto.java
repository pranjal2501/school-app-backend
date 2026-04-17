package com.example.school_management.result.dto;

import com.example.school_management.common.enums.ExamTerm;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class MarkUploadRequestDto {
    @NotNull(message = "Allocation ID (ClassSubjectTeacher) is required.")
    private Long allocationId;

    @NotNull(message = "Exam Term is required.")
    private ExamTerm examTerm;

    @NotEmpty(message = "You must provide marks for at least one student.")
    @Valid
    private List<StudentMarkEntryDto> marks;
}
