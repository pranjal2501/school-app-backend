package com.example.school_management.parent.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class ChildDetailsResponseDto {
    // ── Academic & Attendance ──
    private String regId;
    private String academicYear;
    private Float attendancePercentage;

    // ── Personal Details ──
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private LocalDate dob;
    private String bloodGroup;
    private String aadharNo;
    private String nationality;
    private String religion;
    private String category;

    // ── Class & Teacher Details ──
    private String standard;
    private Integer rollNumber;
    private String className;

    // Teacher Details (Nullable if no teacher assigned)
    private String classTeacherName;
    private String classTeacherEmail;
    private String classTeacherMobile;
    private String classTeacherDegree;

    // ── Previous History ──
    private String previousSchool;
    private Double previousPercentage;

    // ── Documents ──
    private String birthCertificateUrl;
    private String aadharPhotoUrl;
    private String previousMarksheetUrl;
    private String passportPhotoUrl;
    private String leavingCertificateUrl;
    private String casteCertificateUrl;
    private String incomeCertificateUrl;
}