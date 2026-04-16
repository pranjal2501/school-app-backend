package com.example.school_management.complaint.service;

import com.example.school_management.complaint.dto.ComplaintRequestDto;
import com.example.school_management.complaint.dto.ComplaintResponseDto;
import com.example.school_management.complaint.dto.ComplaintStatusUpdateDto;
import jakarta.validation.Valid;

public interface ComplaintService {

    ComplaintResponseDto raiseComplaint(ComplaintRequestDto request);
    ComplaintResponseDto updateComplaintStatus(String ticketNumber, @Valid ComplaintStatusUpdateDto request);
}