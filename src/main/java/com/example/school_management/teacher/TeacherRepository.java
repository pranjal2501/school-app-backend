package com.example.school_management.teacher;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    boolean existsByEmail(String email);
    boolean existsByMobile(String mobile);
    boolean existsByAadharNo(String mobile);
    boolean existsByPanNo(String mobile);
    Optional<Teacher> findByEmail(String email);
    Optional<Teacher> findByMobile(String mobile);
}