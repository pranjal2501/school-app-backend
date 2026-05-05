package com.example.school_management.fees.repository;

import com.example.school_management.fees.entity.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {

    Optional<FeeStructure> findByStandardAndAcademicYear(String standard, String academicYear);
    boolean existsByStandardAndAcademicYear(String standard, String academicYear);
    List<FeeStructure> findAllByOrderByCreatedAtDesc();
}
