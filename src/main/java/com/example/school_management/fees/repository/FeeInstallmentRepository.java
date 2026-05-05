package com.example.school_management.fees.repository;

import com.example.school_management.common.enums.fees.FeeInstallmentStatus;
import com.example.school_management.fees.entity.FeeInstallment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FeeInstallmentRepository extends JpaRepository<FeeInstallment, Long> {

    List<FeeInstallment> findByFeeAllocation_Id(Long allocationId);
    List<FeeInstallment> findByStatusAndDueDateBefore(FeeInstallmentStatus status, LocalDate date);
    boolean existsByFeeAllocation_IdAndStatusNot(Long allocationId, FeeInstallmentStatus status);
}
