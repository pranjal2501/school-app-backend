package com.example.school_management.grievance.template.mapper;

import com.example.school_management.grievance.template.dto.LetterheadTemplateRequestDto;
import com.example.school_management.grievance.template.dto.LetterheadTemplateResponseDto;
import com.example.school_management.grievance.template.entity.LetterheadTemplate;
import org.springframework.stereotype.Component;

@Component
public class LetterheadTemplateMapper {

    public LetterheadTemplate toEntity(LetterheadTemplateRequestDto dto) {
        return LetterheadTemplate.builder()
                .type(dto.getType())
                .letterheadUrl(dto.getLetterheadUrl())
                .content(dto.getContent())
                .fontSize(dto.getFontSize())
                .textColor(dto.getTextColor())
                .textXPosition(dto.getTextXPosition())
                .textYPosition(dto.getTextYPosition())
                .isActive(dto.isActive())
                .build();
    }

    public LetterheadTemplateResponseDto toDto(LetterheadTemplate entity) {
        return LetterheadTemplateResponseDto.builder()
                .id(entity.getId())
                .type(entity.getType())
                .displayName(entity.getType().getDisplayName())
                .letterheadUrl(entity.getLetterheadUrl())
                .content(entity.getContent())
                .isActive(entity.isActive())
                .fontSize(entity.getFontSize())
                .textColor(entity.getTextColor())
                .textXPosition(entity.getTextXPosition())
                .textYPosition(entity.getTextYPosition())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntity(LetterheadTemplate entity, LetterheadTemplateRequestDto dto) {
        entity.setType(dto.getType());
        entity.setLetterheadUrl(dto.getLetterheadUrl());
        entity.setContent(dto.getContent());
        entity.setFontSize(dto.getFontSize());
        entity.setTextColor(dto.getTextColor());
        entity.setTextXPosition(dto.getTextXPosition());
        entity.setTextYPosition(dto.getTextYPosition());
        entity.setActive(dto.isActive());
    }
}