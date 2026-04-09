package com.example.school_management.subject;

import com.example.school_management.subject.dto.SubjectRequestDto;
import com.example.school_management.subject.dto.SubjectResponseDto;

import java.util.List;

public interface SubjectService
{
    SubjectResponseDto createSubject(SubjectRequestDto request);
    List<SubjectResponseDto> getAllSubjects();
    SubjectResponseDto getSubjectByCode(Integer subjectCode);
    SubjectResponseDto updateSubject(Integer subjectCode, SubjectRequestDto request);
    void deleteSubject(Integer subjectCode);

    SubjectResponseDto getSubjectById(Long id);
}