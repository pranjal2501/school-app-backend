package com.example.school_management.assignment.repository;

import com.example.school_management.assignment.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findAllByAllocation_SchoolClass_Id(Long classId);
}