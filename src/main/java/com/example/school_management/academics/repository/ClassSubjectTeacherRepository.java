package com.example.school_management.academics.repository;

import com.example.school_management.academics.entity.ClassSubjectTeacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClassSubjectTeacherRepository extends JpaRepository<ClassSubjectTeacher, Long> {
    // Used to find existing allocations so we can update them safely
    Optional<ClassSubjectTeacher> findBySchoolClassIdAndSubjectId(Long classId, Long subjectId);

    List<ClassSubjectTeacher> findAllBySchoolClassId(Long classId);
    List<ClassSubjectTeacher> findAllBySubject_Standard(String standard);
    // Used to populate the Teacher's Dropdown
    List<ClassSubjectTeacher> findAllByTeacherId(Long teacherId);

    @Query("SELECT COUNT(DISTINCT cst.subject.id) FROM ClassSubjectTeacher cst WHERE cst.teacher.id = :teacherId")
    Integer countDistinctSubjectsByTeacherId(@Param("teacherId") Long teacherId);

    List<ClassSubjectTeacher> findBySchoolClassId(Long classId);

    @Query("SELECT cst FROM ClassSubjectTeacher cst " +
            "JOIN FETCH cst.subject " +
            "JOIN FETCH cst.teacher " +
            "LEFT JOIN FETCH cst.schoolClass " +
            "WHERE cst.schoolClass.id = :classId")
    List<ClassSubjectTeacher> findBySchoolClassIdWithDetails(@Param("classId") Long classId);
}