package com.example.school_management.grievance.template.util;

import com.example.school_management.grievance.template.dto.PlaceholderContext;
import com.example.school_management.parent.entity.Parent;
import com.example.school_management.student.entity.Student;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PlaceholderContextBuilder {

    // ── Bonafide ───────────────────────────────────────────────────────────────
    public PlaceholderContext forBonafide(Student student, String purpose) {
        return base(student)
                .bonafidePurpose(purpose)
                .build();
    }

    // ── LC ─────────────────────────────────────────────────────────────────────
    public PlaceholderContext forLC(Student student, String leavingDate,
                                    String reason, String conduct, String lastAttendedYear) {
        return base(student)
                .lcLeavingDate(leavingDate)
                .lcReason(reason)
                .lcConduct(conduct)
                .lcLastAttendedYear(lastAttendedYear)
                .build();
    }

    // ── Any other certificate (sports, character, participation etc.) ──────────
    public PlaceholderContext forCertificate(Student student) {
        return base(student).build();
    }

    // ── Base — common student + parent fields ──────────────────────────────────
    private PlaceholderContext.PlaceholderContextBuilder base(Student student) {
        Parent parent = student.getParent();
        int year = LocalDate.now().getYear();

        return PlaceholderContext.builder()
                // Student Basic
                .studentName(student.getFirstName() + " " + student.getLastName())
                .studentFirstName(student.getFirstName())
                .studentMiddleName(student.getMiddleName())
                .studentLastName(student.getLastName())
                .studentGender(student.getGender() != null ? student.getGender().name() : null)
                .studentDob(student.getDob() != null ? student.getDob().toString() : null)
                .studentBloodGroup(student.getBloodGroup() != null ? student.getBloodGroup().name() : null)
                .studentNationality(student.getNationality())
                .studentReligion(student.getReligion() != null ? student.getReligion().name() : null)
                .studentCategory(student.getCategory() != null ? student.getCategory().name() : null)
                .studentAadharNo(student.getAadharNo())
                .studentRegId(student.getRegId())

                // Student Academic
                .studentRollNumber(student.getRollNumber() != null ? String.valueOf(student.getRollNumber()) : null)
                .studentClass(student.getAssignedClass() != null ? student.getAssignedClass().getClassName() : null)
                .studentDivision(student.getAssignedClass() != null && student.getAssignedClass().getAcademicStructure() != null
                        ? student.getAssignedClass().getAcademicStructure().getDivision() : null)
                .studentStandard(student.getStandard())
                .studentAcademicYear(year + "-" + (year + 1))
                .studentPreviousSchool(student.getPreviousSchool())

                // Parent
                .fatherName(parent != null ? parent.getFatherFirstName() + " " + parent.getFatherLastName() : null)
                .fatherOccupation(parent != null ? parent.getFatherOccupation() : null)
                .motherName(parent != null ? parent.getMotherFirstName() + " " + parent.getMotherLastName() : null)
                .motherOccupation(parent != null ? parent.getMotherOccupation() : null)
                .guardianName(parent != null && parent.getGuardianFirstName() != null
                        ? parent.getGuardianFirstName() + " " + parent.getGuardianLastName() : null)
                .guardianRelation(parent != null ? parent.getGuardianRelation() : null)
                .parentAddress(parent != null ? buildAddress(parent) : null)
                .parentCity(parent != null ? parent.getCity() : null)
                .parentState(parent != null ? parent.getState() : null)
                .parentPincode(parent != null ? parent.getPincode() : null)
                .parentMobile(parent != null ? parent.getMobilePrimary() : null)
                .parentEmail(parent != null ? parent.getEmail() : null)

                // General
                .currentDate(LocalDate.now().toString())
                .currentYear(String.valueOf(year));
    }

    private String buildAddress(Parent parent) {
        StringBuilder sb = new StringBuilder();
        if (parent.getAddressLine1() != null) sb.append(parent.getAddressLine1());
        if (parent.getAddressLine2() != null) sb.append(", ").append(parent.getAddressLine2());
        return sb.isEmpty() ? null : sb.toString();
    }
}