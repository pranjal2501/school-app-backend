package com.example.school_management.schoolClass.repo;

import com.example.school_management.common.enums.Standard;
import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.teacher.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {
    List<SchoolClass> findAllByStandard(Standard standard);
    Optional<SchoolClass> findByClassName(String className);
    Optional<SchoolClass> findByClassTeacher(Teacher teacher);
}