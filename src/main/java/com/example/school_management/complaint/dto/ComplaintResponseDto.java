package com.example.school_management.complaint.dto;

import com.example.school_management.common.enums.ComplaintCategory;
import com.example.school_management.common.enums.ComplaintStatus;
import com.example.school_management.common.enums.Priority;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // Automatically strips out any null fields from the JSON response
public class ComplaintResponseDto {

    // --- Core Identifiers & Metadata ---
    private String ticketNumber;
    private Instant createdAt;
    private Instant updatedAt;

    // --- Primary Complaint Details ---
    private String title;
    private String description;
    private ComplaintCategory category;
    private Priority priority;
    private ComplaintStatus status;
    private List<String> supportedMedia;

    // --- Target Information (The Accused) ---
    private Long targetStudentId;
    private String targetStudentName;
    private Integer targetStudentRollNumber; // Very helpful for frontend tables

    // --- Reporter Information (The Accuser) ---
    private Long raisedById;
    private String raisedByName;
    private String raisedByRole; // Indicates if it was a TEACHER or ADMIN

    // --- Resolution Information (Populated only when resolved) ---
    private String resolutionComment;
    private String resolvedByName;
    private Instant resolvedAt;
}