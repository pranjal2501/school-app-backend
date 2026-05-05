package com.example.school_management.fees.repository;

import com.example.school_management.fees.entity.InstallmentPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstallmentPlanRepository extends JpaRepository<InstallmentPlan, Long> {

    Optional<InstallmentPlan> findByIdAndFeeStructure_Id(Long planId, Long feeStructureId);
}
