package com.example.school_management.syllabus.repository;

import com.example.school_management.syllabus.entity.Syllabus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SyllabusRepository extends JpaRepository<Syllabus, Long> {
    Optional<Syllabus> findByStandardAndSubjectId(String standard, Long subjectId);
}