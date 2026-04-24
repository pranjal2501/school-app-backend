package com.example.school_management.result.repository;

import com.example.school_management.common.enums.ExamTerm;
import com.example.school_management.result.entity.ResultLetterhead;
import com.example.school_management.result.entity.StudentSubjectMark;
import com.example.school_management.student.entity.Student;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentSubjectMarkRepository extends JpaRepository<StudentSubjectMark, Long> {

    // Used to load the grid for the teacher to view/edit
    List<StudentSubjectMark> findAllByAllocationIdAndExamTerm(Long allocationId, ExamTerm examTerm);

    // Used during upsert to check if a student already has marks for this exam to prevent Unique Constraint crashes
    Optional<StudentSubjectMark> findByStudentIdAndAllocationIdAndExamTerm(Long studentId, Long allocationId, ExamTerm examTerm);

    @Query("SELECT COUNT(m) FROM StudentSubjectMark m WHERE m.allocation.id = :allocationId AND m.examTerm = :examTerm")
    int countByAllocationIdAndExamTerm(
            @Param("allocationId") Long allocationId,
            @Param("examTerm") ExamTerm examTerm);

    List<StudentSubjectMark> findByStudentAndExamTerm(Student student, ExamTerm examTerm);
}