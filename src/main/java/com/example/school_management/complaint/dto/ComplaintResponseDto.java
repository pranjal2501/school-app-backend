package com.example.school_management.complaint.dto;

import com.example.school_management.common.enums.ComplaintCategory;
import com.example.school_management.common.enums.ComplaintStatus;
import com.example.school_management.common.enums.Priority;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class ComplaintResponseDto {

    private Long id;
    private String title;
    private String description;
    private ComplaintCategory category;
    private ComplaintStatus status;
    private Priority priority;
    private List<String> supportedMedia;
    private String resolutionComment;
    private String resolvedBy;
    private Instant resolvedAt;

    private Long studentId;
    private String studentName;

    private Long teacherId;
    private String teacherName;

    private Instant createdAt;
    private Instant updatedAt;
}