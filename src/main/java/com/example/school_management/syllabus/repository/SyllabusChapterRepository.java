package com.example.school_management.syllabus.repository;

import com.example.school_management.syllabus.entity.SyllabusChapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SyllabusChapterRepository extends JpaRepository<SyllabusChapter, Long> {
    // Counts chapters that are directly under a section
    long countBySectionId(Long sectionId);

    // Counts chapters that are nested under a sub-section
    long countBySubSectionId(Long subSectionId);
}