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
                // Student and Employee are strictly set in the Service layer
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

        // --- Map Target Student Safely ---
        if (entity.getTargetStudent() != null) {
            response.setTargetStudentId(entity.getTargetStudent().getId());
            response.setTargetStudentName(entity.getTargetStudent().getFirstName() + " " + entity.getTargetStudent().getLastName());

            // Helpful for frontend grids/tables so they don't have to make a second API call
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

        // 1. Check if a Teacher resolved it
        if (entity.getResolvedBy() != null) {
            response.setResolvedByName(entity.getResolvedBy().getFirstName() + " " + entity.getResolvedBy().getLastName());
        }
        // 2. Check if an Admin resolved it (MUST have the null check!)
        else if (entity.getResolvedByAdmin() != null) {
            response.setResolvedByName(entity.getResolvedByAdmin().getUsername());
        }

        return response;
    }
}