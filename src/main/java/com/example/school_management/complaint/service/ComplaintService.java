package com.example.school_management.complaint.service;

import com.example.school_management.complaint.dto.ComplaintRequestDto;
import com.example.school_management.complaint.dto.ComplaintResponseDto;
import com.example.school_management.complaint.dto.ComplaintStatusUpdateDto;
import com.example.school_management.complaint.dto.VisibilityUpdateDto;
import jakarta.validation.Valid;

import java.util.List;

public interface ComplaintService {

    List<ComplaintResponseDto> raiseComplaint(ComplaintRequestDto request);
    ComplaintResponseDto updateComplaintStatus(String ticketNumber, @Valid ComplaintStatusUpdateDto request);
    List<ComplaintResponseDto> getMyRaisedComplaints();
    List<ComplaintResponseDto> getComplaintsForMyClass();
    ComplaintResponseDto updateComplaintVisibility(String ticketNumber, VisibilityUpdateDto request);
    List<ComplaintResponseDto> getComplaintsForParent(String regId);
    List<ComplaintResponseDto> getComplaintsByClassId(Long classId);
}