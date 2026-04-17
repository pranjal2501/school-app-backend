package com.example.school_management.grievance.bonafide.repository;

import com.example.school_management.grievance.bonafide.entity.BonafideTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BonafideTemplateRepository extends JpaRepository<BonafideTemplate, Long> {
    Optional<BonafideTemplate> findByIsActiveTrue();
}
