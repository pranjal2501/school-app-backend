package com.example.school_management.repo;

import com.example.school_management.entity.Class;
import com.example.school_management.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClassRepository extends JpaRepository<Class, Long> {
    Optional<Class> findByClassTeacher(Teacher teacher);
}