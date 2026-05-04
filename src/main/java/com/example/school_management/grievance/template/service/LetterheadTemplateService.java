package com.example.school_management.grievance.template.service;

import com.example.school_management.admin.repository.AdminRepository;
import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.enums.LetterheadType;
import com.example.school_management.common.exception.UnauthorizedAccessException;
import com.example.school_management.grievance.template.dto.LetterheadTemplateRequestDto;
import com.example.school_management.grievance.template.dto.LetterheadTemplateResponseDto;
import com.example.school_management.grievance.template.entity.LetterheadTemplate;
import com.example.school_management.grievance.template.mapper.LetterheadTemplateMapper;
import com.example.school_management.grievance.template.repository.LetterheadTemplateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LetterheadTemplateService {

    private final LetterheadTemplateRepository repository;
    private final LetterheadTemplateMapper mapper;
    private final RoleVerifier roleVerifier;
    private final AdminRepository adminRepository;


    public LetterheadTemplateResponseDto create(LetterheadTemplateRequestDto request) {
        verifyAdmin();

        if (repository.existsByType(request.getType())) {
            throw new IllegalArgumentException(
                    "Template for type " + request.getType().getDisplayName() + " already exists"
            );
        }
        LetterheadTemplate entity = mapper.toEntity(request);
        return mapper.toDto(repository.save(entity));
    }

    public List<LetterheadTemplateResponseDto> getAll() {
        verifyAdmin();

        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public LetterheadTemplateResponseDto getById(Long id) {
        verifyAdmin();

        return mapper.toDto(findById(id));
    }

    public LetterheadTemplateResponseDto getByType(LetterheadType type) {
        verifyAdmin();

        return repository.findByType(type)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Template for type " + type.getDisplayName() + " not found"
                ));
    }

    public LetterheadTemplateResponseDto update(Long id, LetterheadTemplateRequestDto request) {
        verifyAdmin();

        LetterheadTemplate entity = findById(id);

        // if type is being changed, make sure new type doesn't already have a template
        if (!entity.getType().equals(request.getType()) && repository.existsByType(request.getType())) {
            throw new IllegalArgumentException(
                    "Template for type " + request.getType().getDisplayName() + " already exists"
            );
        }

        mapper.updateEntity(entity, request);
        return mapper.toDto(repository.save(entity));
    }

    public void delete(Long id) {
        verifyAdmin();

        repository.delete(findById(id));
    }

    // internal reusable fetch
    private LetterheadTemplate findById(Long id) {
        verifyAdmin();

        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Template not found with id: " + id));
    }

    public LetterheadTemplate getByTypeEntity(LetterheadType type) {
        return repository.findByType(type)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Template for type " + type.getDisplayName() + " not found"));
    }

    public void verifyAdmin()
    {
       String currentUser= roleVerifier.getCurrentUsername();
       if(!adminRepository.findByUsername(currentUser).isPresent())
       {
           throw new UnauthorizedAccessException("Only Admin can access");
       }
    }
}