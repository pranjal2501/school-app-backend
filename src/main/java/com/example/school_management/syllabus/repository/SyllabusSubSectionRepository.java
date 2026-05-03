package com.example.school_management.syllabus.repository;

import com.example.school_management.syllabus.entity.SyllabusSubSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SyllabusSubSectionRepository extends JpaRepository<SyllabusSubSection, Long> {
    // Highly optimized DB count query: SELECT COUNT(*) FROM syllabus_sub_sections WHERE section_id = ?
    long countBySectionId(Long sectionId);
}