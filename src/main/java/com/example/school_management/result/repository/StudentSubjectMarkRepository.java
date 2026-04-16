package com.example.school_management.result.repository;

import com.example.school_management.common.enums.ExamTerm;
import com.example.school_management.result.entity.StudentSubjectMark;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StudentSubjectMarkRepository extends JpaRepository<StudentSubjectMark, Long> {

    // Used to load the grid for the teacher to view/edit
    List<StudentSubjectMark> findAllByAllocationIdAndExamTerm(Long allocationId, ExamTerm examTerm);

    // Used during upsert to check if a student already has marks for this exam to prevent Unique Constraint crashes
    Optional<StudentSubjectMark> findByStudentIdAndAllocationIdAndExamTerm(Long studentId, Long allocationId, ExamTerm examTerm);
}