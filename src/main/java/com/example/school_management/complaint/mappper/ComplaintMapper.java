package com.example.school_management.complaint.mappper;

import com.example.school_management.complaint.dto.ComplaintRequestDto;
import com.example.school_management.complaint.dto.ComplaintResponseDto;
import com.example.school_management.complaint.entity.Complaint;
import org.springframework.stereotype.Component;

@Component
public class ComplaintMapper {

    public Complaint toEntity(ComplaintRequestDto dto) {
        if (dto == null) return null;

        return Complaint.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .priority(dto.getPriority())
                .supportedMedia(dto.getSupportedMediaUrls())
                // 🚀 MAP THE NEW FIELD (Fallback to true just in case)
                .isVisibleToParents(dto.getIsVisibleToParents() != null ? dto.getIsVisibleToParents() : true)
                .build();
    }

    public ComplaintResponseDto toResponseDto(Complaint entity) {
        if (entity == null) return null;

        ComplaintResponseDto response = new ComplaintResponseDto();

        // --- Core Identifiers & Metadata ---
        response.setTicketNumber(entity.getTicketNumber());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());

        // --- Primary Complaint Details ---
        response.setTitle(entity.getTitle());
        response.setDescription(entity.getDescription());
        response.setCategory(entity.getCategory());
        response.setStatus(entity.getStatus());
        response.setPriority(entity.getPriority());
        response.setSupportedMedia(entity.getSupportedMedia());

        // 🚀 MAP THE NEW FIELD TO RESPONSE
        response.setVisibleToParents(entity.isVisibleToParents());

        // --- Map Target Student Safely ---
        if (entity.getTargetStudent() != null) {
            response.setTargetStudentId(entity.getTargetStudent().getId());
            response.setTargetStudentName(entity.getTargetStudent().getFirstName() + " " + entity.getTargetStudent().getLastName());
            response.setTargetStudentRollNumber(entity.getTargetStudent().getRollNumber());
        }

        // Map Employee Raiser
        if (entity.getReportedByEmployee() != null) {
            response.setRaisedById(entity.getReportedByEmployee().getId());
            response.setRaisedByName(entity.getReportedByEmployee().getFirstName() + " " + entity.getReportedByEmployee().getLastName());
            response.setRaisedByRole(entity.getReportedByEmployee().getRole().name());
        }
        // Map Admin Raiser
        else if (entity.getReportedByAdmin() != null) {
            response.setRaisedById(entity.getReportedByAdmin().getId());
            response.setRaisedByName(entity.getReportedByAdmin().getUsername());
            response.setRaisedByRole("ADMIN");
        }

        response.setResolutionComment(entity.getResolutionComment());
        response.setResolvedAt(entity.getResolvedAt());

        if (entity.getResolvedBy() != null) {
            response.setResolvedByName(entity.getResolvedBy().getFirstName() + " " + entity.getResolvedBy().getLastName());
        } else if (entity.getResolvedByAdmin() != null) {
            response.setResolvedByName(entity.getResolvedByAdmin().getUsername());
        }

        if (entity.getTargetStudent() != null) {
            response.setTargetStudentId(entity.getTargetStudent().getId());
            response.setTargetStudentName(entity.getTargetStudent().getFirstName() + " " + entity.getTargetStudent().getLastName());
            response.setTargetStudentRollNumber(entity.getTargetStudent().getRollNumber());
            if (entity.getTargetStudent().getAssignedClass() != null) {
                response.setTargetStudentClass(
                        entity.getTargetStudent().getAssignedClass().getAcademicStructure().getStandard() + "-" +
                                entity.getTargetStudent().getAssignedClass().getAcademicStructure().getDivision()
                );
            }
        }
        return response;
    }
}