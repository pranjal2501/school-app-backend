package com.example.school_management.grievance.bonafide.mapper;

import com.example.school_management.grievance.bonafide.dto.BonafideRequestResponse;
import com.example.school_management.grievance.bonafide.dto.BonafideTemplateRequest;
import com.example.school_management.grievance.bonafide.dto.BonafideTemplateResponse;
import com.example.school_management.grievance.bonafide.entity.BonafideRequest;
import com.example.school_management.grievance.bonafide.entity.BonafideTemplate;
import org.springframework.stereotype.Component;

@Component
public class BonafideMapper {

    public BonafideRequestResponse toResponse(BonafideRequest req) {
        String studentName = req.getStudent().getFirstName() + " " + req.getStudent().getLastName();
        String studentClass = req.getStudent().getAssignedClass().getClassName();
        String parentName = req.getRequestedBy().getFatherFirstName() + " " + req.getRequestedBy().getFatherLastName();

        return BonafideRequestResponse.builder()
                .id(req.getId())
                .studentId(req.getStudent().getId())
                .studentName(studentName)
                .studentClass(studentClass)
                .requestedById(req.getRequestedBy().getId())
                .requestedByName(parentName)
                .status(req.getStatus())
                .purpose(req.getPurpose())
                .remarks(req.getRemarks())
                .requestedAt(req.getRequestedAt())
                .resolvedAt(req.getResolvedAt())
                .build();
    }

    public BonafideTemplateResponse toTemplateResponse(BonafideTemplate template) {
        return BonafideTemplateResponse.builder()
                .id(template.getId())
                .letterheadUrl(template.getLetterheadUrl())
                .content(template.getContent())
                .isActive(template.isActive())
                .fontSize(template.getFontSize())
                .textColor(template.getTextColor())
                .textXPosition(template.getTextXPosition())
                .textYPosition(template.getTextYPosition())
                .createdAt(template.getCreatedAt())
                .build();
    }

    public BonafideTemplate toTemplateEntity(BonafideTemplateRequest req) {
        return BonafideTemplate.builder()
                .letterheadUrl(req.getLetterheadUrl())
                .content(req.getContent())
                .fontSize(req.getFontSize())
                .textColor(req.getTextColor())
                .textXPosition(req.getTextXPosition())
                .textYPosition(req.getTextYPosition())
                .isActive(true)
                .build();
    }
}


