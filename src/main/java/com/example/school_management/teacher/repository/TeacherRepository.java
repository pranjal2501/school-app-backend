package com.example.school_management.teacher.repository;

import com.example.school_management.teacher.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Teacher t " +
            "WHERE t.id = :teacherId AND (t.email = :username OR t.mobile = :username)")
        // NOTE: Adjust 't.mobile' if your entity uses a different name like 't.phone'
    boolean isTeacherAuthorized(@Param("teacherId") Long teacherId, @Param("username") String username);

    @Query("SELECT t.id FROM Teacher t WHERE t.email = :username OR t.mobile = :username")
    Optional<Long> findIdByUsername(@Param("username") String username);
}