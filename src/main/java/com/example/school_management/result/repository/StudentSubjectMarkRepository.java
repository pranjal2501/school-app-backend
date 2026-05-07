package com.example.school_management.result.repository;

import com.example.school_management.result.entity.StudentSubjectMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentSubjectMarkRepository extends JpaRepository<StudentSubjectMark, Long> {

    // Used when a teacher is updating a specific student's specific component mark
    Optional<StudentSubjectMark> findByStudentIdAndAllocationIdAndExamTypeIdAndEvaluationComponentId(
            Long studentId, Long allocationId, Long examTypeId, Long evaluationComponentId);

    // Used by Teacher to view the grid of all marks for a specific class subject and exam
    List<StudentSubjectMark> findAllByAllocationIdAndExamTypeId(Long allocationId, Long examTypeId);

    // Used by Admin to check how many unique students have been graded for a specific subject and exam
    @Query("SELECT COUNT(DISTINCT m.student.id) FROM StudentSubjectMark m WHERE m.allocation.id = :allocationId AND m.examType.id = :examTypeId")
    int countDistinctStudentsByAllocationIdAndExamTypeId(@Param("allocationId") Long allocationId, @Param("examTypeId") Long examTypeId);

    // Used by Parent/Result generator to pull all component marks for a specific student's exam
    List<StudentSubjectMark> findAllByStudentIdAndExamTypeId(Long studentId, Long examTypeId);
}