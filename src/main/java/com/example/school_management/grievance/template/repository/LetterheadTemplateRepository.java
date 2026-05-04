package com.example.school_management.grievance.template.repository;

import com.example.school_management.common.enums.LetterheadType;
import com.example.school_management.grievance.template.entity.LetterheadTemplate;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LetterheadTemplateRepository extends JpaRepository<LetterheadTemplate, Long> {
    boolean existsByType(@NotNull(message = "Letterhead type is required") LetterheadType type);

    Optional<LetterheadTemplate> findByType(LetterheadType type);
}
