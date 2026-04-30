package com.example.school_management.teacher.repository;

import com.example.school_management.teacher.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    boolean existsByEmail(String email);
    boolean existsByMobile(String mobile);
    boolean existsByAadharNo(String mobile);
    boolean existsByPanNo(String mobile);
    Optional<Teacher> findByEmail(String email);
    Optional<Teacher> findByMobile(String mobile);

    // Fetch teachers who do not have an assigned class
    List<Teacher> findByAssignedClassIsNullAndIsActiveTrue();

    long countByIsActiveTrue();

    long countByIsActiveTrueAndCreatedAtAfter(Instant thisMonthStart);

    long countByIsActiveTrueAndCreatedAtBetween(Instant lastMonthStart, Instant lastMonthEnd);
}