package com.example.school_management.academics.repository;

import com.example.school_management.academics.entity.ClassSubjectTeacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassSubjectTeacherRepository extends JpaRepository<ClassSubjectTeacher, Long> {
    // Used to find existing allocations so we can update them safely
    Optional<ClassSubjectTeacher> findBySchoolClassIdAndSubjectId(Long classId, Long subjectId);

    List<ClassSubjectTeacher> findAllBySchoolClassId(Long classId);
    List<ClassSubjectTeacher> findAllBySubject_Standard(String standard);
    // Used to populate the Teacher's Dropdown
    List<ClassSubjectTeacher> findAllByTeacherId(Long teacherId);
}