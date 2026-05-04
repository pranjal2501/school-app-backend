package com.example.school_management.grievance.bonafide.mapper;

import com.example.school_management.grievance.bonafide.dto.BonafideRequestResponse;
import com.example.school_management.grievance.bonafide.entity.BonafideRequest;
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
}


