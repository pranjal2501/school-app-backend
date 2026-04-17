package com.example.school_management.grievance.bonafide.service;

import com.example.school_management.grievance.bonafide.mapper.BonafideMapper;
import com.example.school_management.grievance.bonafide.dto.BonafideTemplateRequest;
import com.example.school_management.grievance.bonafide.dto.BonafideTemplateResponse;
import com.example.school_management.grievance.bonafide.entity.BonafideTemplate;
import com.example.school_management.grievance.bonafide.repository.BonafideTemplateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BonafideTemplateService {

    private final BonafideTemplateRepository templateRepository;
    private final BonafideMapper bonafideMapper;

    @Transactional
    public BonafideTemplateResponse createTemplate(BonafideTemplateRequest request) {
        // deactivate existing active template if present
        templateRepository.findByIsActiveTrue()
                .ifPresent(existing -> {
                    existing.setActive(false);
                    templateRepository.save(existing);
                });

        BonafideTemplate template = bonafideMapper.toTemplateEntity(request);
        BonafideTemplate saved = templateRepository.save(template);
        return bonafideMapper.toTemplateResponse(saved);
    }

    @Transactional
    public BonafideTemplateResponse updateActiveTemplate(BonafideTemplateRequest request) {
        BonafideTemplate template = getActiveTemplateEntity();

        template.setLetterheadUrl(request.getLetterheadUrl());
        template.setContent(request.getContent());
        template.setFontSize(request.getFontSize());
        template.setTextColor(request.getTextColor());
        template.setTextXPosition(request.getTextXPosition());
        template.setTextYPosition(request.getTextYPosition());

        return bonafideMapper.toTemplateResponse(templateRepository.save(template));
    }

    @Transactional(readOnly = true)
    public BonafideTemplateResponse getActiveTemplate() {
        return bonafideMapper.toTemplateResponse(getActiveTemplateEntity());
    }

    // ── internal helper used by BonafideRequestService during approval ─────────

    BonafideTemplate getActiveTemplateEntity() {
        return templateRepository.findByIsActiveTrue()
                .orElseThrow(() -> new EntityNotFoundException(
                        "No active bonafide template found. Admin must create one first."));
    }
}
