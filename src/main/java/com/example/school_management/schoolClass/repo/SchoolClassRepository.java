package com.example.school_management.schoolClass.repo;

import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.teacher.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {
    List<SchoolClass> findAllByAcademicStructure_Standard(String standard);
    Optional<SchoolClass> findByClassName(String className);
    Optional<SchoolClass> findByClassTeacher(Teacher teacher);
    Optional<SchoolClass> findByAcademicStructureId(Long id);

    Optional<SchoolClass> findByAcademicStructure_StandardAndAcademicStructure_Division(String standard, String division);
}