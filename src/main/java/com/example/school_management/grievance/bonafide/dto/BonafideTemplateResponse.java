package com.example.school_management.grievance.bonafide.dto;
//What admin/anyone sees back

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonafideTemplateResponse {

    private Long id;
    private String letterheadUrl;
    private String content;
    private boolean isActive;
    private Integer fontSize;
    private String textColor;
    private Integer textXPosition;
    private Integer textYPosition;
    private LocalDateTime createdAt;

    public static final String PLACEHOLDER_GUIDE =
            "Available: {{student.name}}, {{student.class}}, {{student.division}}, " +
                    "{{student.rollNumber}}, {{student.dob}}, {{student.category}}, " +
                    "{{student.academicYear}}, {{student.religion}}, {{student.nationality}}";
}
