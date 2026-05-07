package com.example.school_management.result.repository;

import com.example.school_management.result.entity.ResultLetterhead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResultLetterHeadRepository extends JpaRepository<ResultLetterhead, Long> {
    Optional<ResultLetterhead> findByExamTypeIdAndAcademicYear(Long examTypeId, String academicYear);
}