package com.example.school_management.subject.service;
import com.example.school_management.academics.repository.ClassSubjectTeacherRepository;
import com.example.school_management.subject.dto.SubjectResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {
    private final ClassSubjectTeacherRepository classSubjectTeacherRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponseDto> getSubjectsByStandard(String standard) {
        return classSubjectTeacherRepository.findDistinctSubjectsByStandard(standard)
                .stream()
                .map(subject -> SubjectResponseDto.builder()
                        .id(subject.getId())
                        .name(subject.getSubjectName()) // Adjust getter based on your Subject entity
                        .build())
                .collect(Collectors.toList());
    }
}