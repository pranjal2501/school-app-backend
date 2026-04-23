package com.example.school_management.assignment.repository;

import com.example.school_management.assignment.entity.Assignment;
import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.teacher.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findAllByAllocation_SchoolClass_Id(Long classId);

    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.allocation.teacher = :teacher AND a.dueDate > :now")
    int countActiveAssignmentsByTeacher(@Param("teacher") Teacher teacher, @Param("now") LocalDateTime now);

    List<Assignment> findAllByAllocation_Teacher_IdOrderByCreatedAtDesc(Long teacherId);

    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.allocation.schoolClass = :schoolClass AND a.dueDate > :now")
    int countPendingAssignmentsByClass(@Param("schoolClass") SchoolClass schoolClass, @Param("now") LocalDateTime now);
}