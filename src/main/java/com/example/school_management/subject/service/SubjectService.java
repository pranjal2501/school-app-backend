package com.example.school_management.subject.service;

import com.example.school_management.subject.dto.BulkSubjectCreateRequestDto;
import com.example.school_management.subject.dto.SubjectResponseDto;
import com.example.school_management.subject.dto.newImpl.SubjectRequestDto;
import com.example.school_management.subject.dto.newImpl.SubjectTreeResponseDto;
import com.example.school_management.subject.entity.Subject;

import java.util.List;

public interface SubjectService{
    List<SubjectResponseDto> getSubjectsByStandard(String standard);
    Subject createSubject(SubjectRequestDto dto);
    List<SubjectTreeResponseDto> getSubjectTreeByStandard(String standard);
    List<Subject> getLeafSubjectsForAllocation(String standard);
    String bulkCreateSubjects(BulkSubjectCreateRequestDto request);
}