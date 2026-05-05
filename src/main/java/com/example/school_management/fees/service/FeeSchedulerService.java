package com.example.school_management.fees.service;


import com.example.school_management.common.enums.fees.FeeAllocationStatus;
import com.example.school_management.common.enums.fees.FeeInstallmentStatus;
import com.example.school_management.fees.entity.FeeAllocation;
import com.example.school_management.fees.entity.FeeInstallment;
import com.example.school_management.fees.repository.FeeAllocationRepository;
import com.example.school_management.fees.repository.FeeInstallmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeeSchedulerService {

    private final FeeInstallmentRepository feeInstallmentRepository;
    private final FeeAllocationRepository feeAllocationRepository;

    // Runs every day at midnight
    @Scheduled(cron = "0 * * * * *")// every minutes
//    @Scheduled(cron = "0 0 0 * * *") // midnight
    @Transactional
    public void markOverdueInstallments() {
        log.info("Running overdue installment check...");

        List<FeeInstallment> overdueInstallments = feeInstallmentRepository
                .findByStatusAndDueDateBefore(FeeInstallmentStatus.PENDING, LocalDate.now());

        if (overdueInstallments.isEmpty()) {
            log.info("No overdue installments found.");
            return;
        }

        overdueInstallments.forEach(i -> i.setStatus(FeeInstallmentStatus.OVERDUE));
        feeInstallmentRepository.saveAll(overdueInstallments);

        log.info("Marked {} installments as OVERDUE.", overdueInstallments.size());
    }

    @Scheduled(cron = "0 5 0 * * *") // 5 minutes after installment job
    @Transactional
    public void markOverdueFeeAllocations() {
        log.info("Running overdue allocation check...");

        List<FeeAllocation> activeAllocations = feeAllocationRepository
                .findByStatus(FeeAllocationStatus.ACTIVE); // add this to repo

        List<FeeAllocation> nowOverdue = activeAllocations.stream()
                .filter(allocation -> {
                    List<FeeInstallment> installments = feeInstallmentRepository
                            .findByFeeAllocation_Id(allocation.getId());
                    return installments.stream()
                            .anyMatch(i -> i.getStatus() == FeeInstallmentStatus.OVERDUE);
                })
                .peek(allocation -> allocation.setStatus(FeeAllocationStatus.OVERDUE))
                .toList();

        if (!nowOverdue.isEmpty()) {
            feeAllocationRepository.saveAll(nowOverdue);
            log.info("Marked {} allocations as OVERDUE.", nowOverdue.size());
        }
    }
}