package com.example.school_management.parent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChildSummaryResponseDto {
    private String registrationId;
    private String name;
    private String schoolClass;
    private Long classId;
    private Integer rollNo;
    private Long parentId;
}