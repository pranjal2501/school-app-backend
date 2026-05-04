package com.example.school_management.grievance.template.dto;

import com.example.school_management.common.enums.LetterheadType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LetterheadTemplateResponseDto {

    private Long id;
    private LetterheadType type;
    private String displayName;         // from type.getDisplayName()
    private String letterheadUrl;
    private String content;
    private boolean isActive;
    private Integer fontSize;
    private String textColor;
    private Integer textXPosition;
    private Integer textYPosition;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}