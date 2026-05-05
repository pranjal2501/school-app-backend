package com.example.school_management.exam.service;

import com.example.school_management.exam.entity.EvaluationComponent;
import com.example.school_management.exam.entity.ExamType;
import com.example.school_management.exam.repository.EvaluationComponentRepository;
import com.example.school_management.exam.repository.ExamTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamSetupService {

    private final ExamTypeRepository examTypeRepository;
    private final EvaluationComponentRepository componentRepository;

    @Transactional
    public ExamType createExamType(String name) {
        ExamType examType = ExamType.builder().name(name).isActive(true).build();
        return examTypeRepository.save(examType);
    }

    public List<ExamType> getAllActiveExamTypes() {
        return examTypeRepository.findAll(); // Add custom query for isActive = true if needed
    }

    @Transactional
    public EvaluationComponent createComponent(String name) {
        EvaluationComponent component = EvaluationComponent.builder().name(name).isActive(true).build();
        return componentRepository.save(component);
    }

    public List<EvaluationComponent> getAllActiveComponents() {
        return componentRepository.findAll();
    }
}