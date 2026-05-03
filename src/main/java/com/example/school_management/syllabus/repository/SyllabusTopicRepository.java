package com.example.school_management.syllabus.repository;

import com.example.school_management.syllabus.entity.SyllabusTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SyllabusTopicRepository extends JpaRepository<SyllabusTopic, Long> {
    long countByChapterId(Long chapterId);
}