package com.example.school_management.complaint;

import com.example.school_management.complaint.dto.ComplaintRequestDto;
import com.example.school_management.complaint.dto.ComplaintResponseDto;
import com.example.school_management.common.enums.ComplaintCategory;
import com.example.school_management.common.enums.ComplaintStatus;
import com.example.school_management.common.enums.Priority;
import java.util.List;

public interface ComplaintService {


    ComplaintResponseDto createComplaint(ComplaintRequestDto request);

    List<ComplaintResponseDto> getAllComplaints();

    ComplaintResponseDto getComplaintById(Long id);

    List<ComplaintResponseDto> getComplaintsByStudent(Long studentId);

    List<ComplaintResponseDto> getComplaintsByTeacher(Long teacherId);

    List<ComplaintResponseDto> getComplaintsByStatus(ComplaintStatus status);

    List<ComplaintResponseDto> getComplaintsByPriority(Priority priority);

    List<ComplaintResponseDto> getComplaintsByCategory(ComplaintCategory category);

    ComplaintResponseDto updateStatus(Long id, ComplaintStatus status);

    ComplaintResponseDto resolveComplaint(Long id, String resolutionComment, String resolvedBy);

    void deleteComplaint(Long id);
}