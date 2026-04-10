package com.example.school_management.complaint;

import com.example.school_management.complaint.dto.ComplaintRequestDto;
import com.example.school_management.complaint.dto.ComplaintResponseDto;
import com.example.school_management.common.enums.ComplaintCategory;
import com.example.school_management.common.enums.ComplaintStatus;
import com.example.school_management.common.enums.Priority;
import com.example.school_management.complaint.dto.ComplaintStatusUpdateDto;
import jakarta.validation.Valid;

import java.util.List;

public interface ComplaintService {

    ComplaintResponseDto raiseComplaint(ComplaintRequestDto request, String userIdentifier, boolean isAdmin);
    ComplaintResponseDto updateComplaintStatus(String ticketNumber, @Valid ComplaintStatusUpdateDto request, String userIdentifier, boolean isAdmin);
}