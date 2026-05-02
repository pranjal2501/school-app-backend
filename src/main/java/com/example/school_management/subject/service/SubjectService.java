package com.example.school_management.subject.service;

import com.example.school_management.subject.dto.SubjectResponseDto;

import java.util.List;

public interface SubjectService{
    List<SubjectResponseDto> getSubjectsByStandard(String standard);
}