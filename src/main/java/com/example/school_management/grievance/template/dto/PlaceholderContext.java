package com.example.school_management.grievance.template.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceholderContext {

    // ── Student Basic Info ─────────────────────────────────────────────────────
    private String studentName;
    private String studentFirstName;
    private String studentMiddleName;
    private String studentLastName;
    private String studentGender;
    private String studentDob;
    private String studentBloodGroup;
    private String studentNationality;
    private String studentReligion;
    private String studentCategory;
    private String studentAadharNo;
    private String studentRegId;

    // ── Student Academic Info ──────────────────────────────────────────────────
    private String studentRollNumber;
    private String studentClass;
    private String studentDivision;
    private String studentStandard;
    private String studentAcademicYear;
    private String studentPreviousSchool;

    // ── Parent Info ────────────────────────────────────────────────────────────
    private String fatherName;
    private String fatherOccupation;
    private String motherName;
    private String motherOccupation;
    private String guardianName;
    private String guardianRelation;
    private String parentAddress;
    private String parentCity;
    private String parentState;
    private String parentPincode;
    private String parentMobile;
    private String parentEmail;

    // ── Bonafide Specific ──────────────────────────────────────────────────────
    private String bonafidePurpose;

    // ── LC Specific ────────────────────────────────────────────────────────────
    private String lcLeavingDate;
    private String lcReason;
    private String lcConduct;
    private String lcLastAttendedYear;

    // ── General ────────────────────────────────────────────────────────────────
    private String currentDate;
    private String currentYear;

    // ── Fee Receipt Specific ────────────────────────────────────────────────
    private String feeInstallmentNumber;
    private String feeTotalInstallments;
    private String feeAmountPaid;
    private String feePaymentDate;
    private String feeTotalFee;
}