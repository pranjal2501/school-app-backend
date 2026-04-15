package com.example.school_management.academics.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AcademicStructureResponseDto {
    private Long id;
    private String standard;
    private String division;
}