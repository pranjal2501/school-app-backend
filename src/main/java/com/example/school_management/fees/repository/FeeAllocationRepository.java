package com.example.school_management.fees.repository;

import com.example.school_management.common.enums.fees.FeeAllocationStatus;
import com.example.school_management.fees.entity.FeeAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeeAllocationRepository extends JpaRepository<FeeAllocation, Long> {

    Optional<FeeAllocation> findByStudent_RegIdAndAcademicYear(String regId, String academicYear);
    List<FeeAllocation> findByFeeStructure_StandardAndAcademicYear(String standard, String academicYear);
    boolean existsByFeeStructure_Id(Long feeStructureId);

    List<FeeAllocation> findByStatus(FeeAllocationStatus feeAllocationStatus);
}
