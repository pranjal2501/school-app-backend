package com.example.school_management.parent.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParentProfileResponseDto {

    private Long parentId;
    // Father's Details
    private String fatherFullName;
    private String fatherOccupation;

    // Mother's Details
    private String motherFullName;
    private String motherOccupation;

    // Contact Info
    private String email;
    private String mobilePrimary;
    private String mobileAlternate;

    // Address
    private String fullAddress;

    // Identity
    private String aadharNo;
    private String aadharOwnerType;

    private String emergencyContactName;
    private String emergencyContactNumber;
}