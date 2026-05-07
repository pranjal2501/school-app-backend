package com.example.school_management.result.dto.resultDisplay;

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
    private Long examTypeId;
    private String academicYear;
}