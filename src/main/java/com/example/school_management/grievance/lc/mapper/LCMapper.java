package com.example.school_management.grievance.lc.mapper;

import com.example.school_management.grievance.lc.dto.LCRequestResponse;
import com.example.school_management.grievance.lc.entity.LCRequest;
import com.example.school_management.student.entity.Student;
import com.example.school_management.parent.entity.Parent;
import org.springframework.stereotype.Component;

@Component
public class LCMapper {

    public LCRequestResponse toResponse(LCRequest entity) {
        Student student = entity.getStudent();
        Parent parent = entity.getRequestedBy();

        return LCRequestResponse.builder()
                .id(entity.getId())

                // Student
                .studentId(student.getId())
                .studentName(student.getFirstName() + " " + student.getLastName())
                .studentRegId(student.getRegId())
                .studentClass(student.getAssignedClass() != null
                        ? student.getAssignedClass().getClassName() : "N/A")
                .studentStandard(student.getStandard())

                // Parent
                .requestedById(parent.getId())
                .requestedByName(parent.getFatherFirstName() + " " + parent.getFatherLastName())

                // Request
                .status(entity.getStatus())
                .reason(entity.getReason())
                .remarks(entity.getRemarks())

                // Approval fields
                .leavingDate(entity.getLeavingDate())
                .conduct(entity.getConduct())
                .lastAttendedYear(entity.getLastAttendedYear())

                // Timestamps
                .requestedAt(entity.getRequestedAt())
                .resolvedAt(entity.getResolvedAt())
                .build();
    }
}