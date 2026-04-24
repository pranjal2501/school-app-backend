package com.example.school_management.result.repository;


import com.example.school_management.common.enums.ExamTerm;
import com.example.school_management.result.entity.ResultLetterhead;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResultLetterHeadRepository extends JpaRepository<ResultLetterhead, Long> {
    Optional<ResultLetterhead> findByExamTermAndAcademicYear(ExamTerm examTerm, String academicYear);
}
