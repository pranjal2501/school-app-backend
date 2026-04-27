package com.example.school_management.result.dto.resultDisplay;

import com.example.school_management.common.enums.ExamTerm;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LetterHeadResponseDto {

    private String url;

    private ExamTerm examTerm;

    private String academicYear;
}
