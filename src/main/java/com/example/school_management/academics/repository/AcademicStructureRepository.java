package com.example.school_management.academics.repository;

import com.example.school_management.academics.entity.AcademicStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AcademicStructureRepository extends JpaRepository<AcademicStructure, Long> {

    Optional<AcademicStructure> findByStandardAndDivision(String standard, String division);

    // 1. Fetches a list of unique standards (e.g., ["6", "7", "8"])
    @Query("SELECT DISTINCT a.standard FROM AcademicStructure a ORDER BY a.standard")
    List<String> findDistinctStandards();

    // 2. Fetches all structures for a specific standard
    List<AcademicStructure> findAllByStandard(String standard);
}