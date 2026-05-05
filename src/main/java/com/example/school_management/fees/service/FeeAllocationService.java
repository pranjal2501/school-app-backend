package com.example.school_management.fees.service;

import com.example.school_management.common.enums.fees.FeeAllocationStatus;
import com.example.school_management.common.enums.fees.FeeInstallmentStatus;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.fees.dto.requestDto.MarkInstallmentPaidRequestDto;
import com.example.school_management.fees.dto.requestDto.SelectInstallmentPlanRequestDto;
import com.example.school_management.fees.dto.responseDto.*;
import com.example.school_management.fees.entity.FeeAllocation;
import com.example.school_management.fees.entity.FeeInstallment;
import com.example.school_management.fees.entity.FeeStructure;
import com.example.school_management.fees.entity.InstallmentPlan;
import com.example.school_management.fees.repository.FeeAllocationRepository;
import com.example.school_management.fees.repository.FeeInstallmentRepository;
import com.example.school_management.fees.repository.FeeStructureRepository;
import com.example.school_management.fees.repository.InstallmentPlanRepository;
import com.example.school_management.student.entity.Student;
import com.example.school_management.student.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FeeAllocationService {

    private final FeeAllocationRepository feeAllocationRepository;
    private final FeeInstallmentRepository feeInstallmentRepository;
    private final FeeStructureRepository feeStructureRepository;
    private final InstallmentPlanRepository installmentPlanRepository;
    private final StudentRepository studentRepository; // your existing repo

    // ─── Called during admission ────────────────────────────────

    public void allocateFee(Long studentId, String academicYear) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

        if (feeAllocationRepository.findByStudent_RegIdAndAcademicYear(
                student.getRegId(), academicYear).isPresent()) {
            return; // already allocated, skip silently
        }

        FeeStructure feeStructure = feeStructureRepository
                .findByStandardAndAcademicYear(student.getStandard(), academicYear)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No fee structure found for standard " + student.getStandard()
                                + " and year " + academicYear));

        FeeAllocation allocation = new FeeAllocation();
        allocation.setStudent(student);
        allocation.setFeeStructure(feeStructure);
        allocation.setAcademicYear(academicYear);
        allocation.setTotalAmount(feeStructure.getTotalAmount());
        allocation.setStatus(FeeAllocationStatus.PLAN_NOT_SELECTED);

        feeAllocationRepository.save(allocation);
    }

    // ─── Parent selects plan ─────────────────────────────────────

    public FeeAllocationResponseDto selectPlan(Long allocationId, SelectInstallmentPlanRequestDto request) throws BadRequestException {
        FeeAllocation allocation = feeAllocationRepository.findById(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("Fee allocation not found: " + allocationId));

        if (allocation.getStatus() != FeeAllocationStatus.PLAN_NOT_SELECTED) {
            throw new BadRequestException("Plan already selected for this allocation.");
        }

        InstallmentPlan plan = installmentPlanRepository
                .findByIdAndFeeStructure_Id(request.getPlanId(), allocation.getFeeStructure().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found: " + request.getPlanId()));

        // Create FeeInstallments from plan schedules
        List<FeeInstallment> installments = plan.getSchedules().stream().map(schedule -> {
            FeeInstallment installment = new FeeInstallment();
            installment.setFeeAllocation(allocation);
            installment.setInstallmentNumber(schedule.getInstallmentNumber());
            installment.setAmount(schedule.getAmount());
            installment.setDueDate(schedule.getDueDate());
            installment.setStatus(FeeInstallmentStatus.PENDING);
            return installment;
        }).toList();

        feeInstallmentRepository.saveAll(installments);

        allocation.setSelectedPlan(plan);
        allocation.setStatus(FeeAllocationStatus.ACTIVE);
        feeAllocationRepository.save(allocation);

        return mapToAllocationResponse(allocation, installments);
    }

    // ─── Admin marks installment paid ────────────────────────────

    public FeeAllocationResponseDto markInstallmentPaid(Long installmentId, MarkInstallmentPaidRequestDto request) throws BadRequestException {
        FeeInstallment installment = feeInstallmentRepository.findById(installmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Installment not found: " + installmentId));

        if (installment.getStatus() == FeeInstallmentStatus.PAID) {
            throw new BadRequestException("Installment is already marked as paid.");
        }

        installment.setStatus(FeeInstallmentStatus.PAID);
        installment.setPaidAt(LocalDateTime.now());
        installment.setRemarks(request.getRemarks());
        feeInstallmentRepository.save(installment);

        // Check if all installments are paid → mark allocation COMPLETED
        FeeAllocation allocation = installment.getFeeAllocation();
        List<FeeInstallment> allInstallments = feeInstallmentRepository
                .findByFeeAllocation_Id(allocation.getId());

        boolean allPaid = allInstallments.stream()
                .allMatch(i -> i.getStatus() == FeeInstallmentStatus.PAID);

        if (allPaid) {
            allocation.setStatus(FeeAllocationStatus.COMPLETED);
            feeAllocationRepository.save(allocation);
        }

        return mapToAllocationResponse(allocation, allInstallments);
    }

    // ─── Admin views all students of a standard ──────────────────

    public List<FeeAllocationResponseDto> getAllAllocationsForStandard(String standard, String academicYear) {
        return feeAllocationRepository
                .findByFeeStructure_StandardAndAcademicYear(standard, academicYear)
                .stream()
                .map(allocation -> {
                    List<FeeInstallment> installments = feeInstallmentRepository
                            .findByFeeAllocation_Id(allocation.getId());
                    return mapToAllocationResponse(allocation, installments);
                })
                .toList();
    }

    // ─── Admin views single student allocation ───────────────────

    public FeeAllocationResponseDto getFeeAllocationForStudent(String regId, String academicYear) {
        FeeAllocation allocation = feeAllocationRepository
                .findByStudent_RegIdAndAcademicYear(regId, academicYear)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No fee allocation found for student: " + regId));

        List<FeeInstallment> installments = feeInstallmentRepository
                .findByFeeAllocation_Id(allocation.getId());

        return mapToAllocationResponse(allocation, installments);
    }

    // ─── Parent view ─────────────────────────────────────────────

    public ParentFeeResponseDto getParentFeeView(String regId) {
        // Get current academic year's allocation
        // You can make academicYear dynamic or pass it in
        FeeAllocation allocation = feeAllocationRepository
                .findByStudent_RegIdAndAcademicYear(regId, getCurrentAcademicYear())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No fee allocation found for student: " + regId));

        List<FeeInstallment> installments = feeInstallmentRepository
                .findByFeeAllocation_Id(allocation.getId());

        return mapToParentFeeResponse(allocation, installments);
    }

    // ─── Helpers ─────────────────────────────────────────────────

    public String getCurrentAcademicYear() {
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();
        // Academic year starts in June
        if (month >= 6) {
            return year + "-" + String.valueOf(year + 1).substring(2);
        } else {
            return (year - 1) + "-" + String.valueOf(year).substring(2);
        }
    }

    private double calculateTotalPaid(List<FeeInstallment> installments) {
        return installments.stream()
                .filter(i -> i.getStatus() == FeeInstallmentStatus.PAID)
                .mapToDouble(FeeInstallment::getAmount)
                .sum();
    }

    // ─── Mappers ─────────────────────────────────────────────────

    private FeeAllocationResponseDto mapToAllocationResponse(
            FeeAllocation allocation, List<FeeInstallment> installments) {

        double totalPaid = calculateTotalPaid(installments);

        FeeAllocationResponseDto dto = new FeeAllocationResponseDto();
        dto.setId(allocation.getId());
        dto.setStudentName(allocation.getStudent().getFirstName()+" "+allocation.getStudent().getLastName());
        dto.setRegistrationId(allocation.getStudent().getRegId());
        dto.setStandard(allocation.getFeeStructure().getStandard());
        dto.setAcademicYear(allocation.getAcademicYear());
        dto.setTotalAmount(allocation.getTotalAmount());
        dto.setTotalPaid(totalPaid);
        dto.setTotalPending(allocation.getTotalAmount() - totalPaid);
        dto.setStatus(allocation.getStatus());
        dto.setSelectedPlan(allocation.getSelectedPlan() != null
                ? allocation.getSelectedPlan().getPlanName() : null);

        dto.setInstallments(installments.stream().map(i -> {
            FeeInstallmentResponseDto idto = new FeeInstallmentResponseDto();
            idto.setId(i.getId());
            idto.setInstallmentNumber(i.getInstallmentNumber());
            idto.setAmount(i.getAmount());
            idto.setDueDate(i.getDueDate());
            idto.setStatus(i.getStatus());
            idto.setPaidAt(i.getPaidAt());
            idto.setRemarks(i.getRemarks());
            idto.setIsOverdue(i.getStatus() == FeeInstallmentStatus.OVERDUE);
            return idto;
        }).toList());

        return dto;
    }

    private ParentFeeResponseDto mapToParentFeeResponse(
            FeeAllocation allocation, List<FeeInstallment> installments) {

        double totalPaid = calculateTotalPaid(installments);
        boolean planSelected = allocation.getStatus() != FeeAllocationStatus.PLAN_NOT_SELECTED;

        ParentFeeResponseDto dto = new ParentFeeResponseDto();
        dto.setStudentName(allocation.getStudent().getFirstName()+" "+allocation.getStudent().getLastName());
        dto.setRegistrationId(allocation.getStudent().getRegId());
        dto.setStandard(allocation.getFeeStructure().getStandard());
        dto.setAcademicYear(allocation.getAcademicYear());
        dto.setTotalFee(allocation.getTotalAmount());
        dto.setTotalPaid(totalPaid);
        dto.setTotalPending(allocation.getTotalAmount() - totalPaid);
        dto.setStatus(allocation.getStatus());
        dto.setPlanSelected(planSelected);

        // Show available plans only when plan not yet selected
        if (!planSelected) {
            dto.setAvailablePlans(
                    allocation.getFeeStructure().getInstallmentPlans().stream().map(p -> {
                        InstallmentPlanResponseDto pdto = new InstallmentPlanResponseDto();
                        pdto.setId(p.getId());
                        pdto.setPlanName(p.getPlanName());
                        pdto.setNumberOfInstallments(p.getNumberOfInstallments());
                        pdto.setDescription(p.getDescription());
                        return pdto;
                    }).toList()
            );
        }

        // Show installments only after plan selected
        if (planSelected) {
            dto.setInstallments(installments.stream().map(i -> {
                FeeInstallmentResponseDto idto = new FeeInstallmentResponseDto();
                idto.setId(i.getId());
                idto.setInstallmentNumber(i.getInstallmentNumber());
                idto.setAmount(i.getAmount());
                idto.setDueDate(i.getDueDate());
                idto.setStatus(i.getStatus());
                idto.setPaidAt(i.getPaidAt());
                idto.setRemarks(i.getRemarks());
                idto.setIsOverdue(i.getStatus() == FeeInstallmentStatus.OVERDUE);
                return idto;
            }).toList());
        }

        // Fee breakdown always shown (for pamphlet)
        dto.setFeeBreakdown(
                allocation.getFeeStructure().getComponents().stream().map(c -> {
                    FeeComponentResponseDto cdto = new FeeComponentResponseDto();
                    cdto.setId(c.getId());
                    cdto.setFeeType(c.getFeeType());
                    cdto.setFeeTypeDisplayName(c.getFeeType().getDisplayName());
                    cdto.setAmount(c.getAmount());
                    cdto.setRemarks(c.getRemarks());
                    return cdto;
                }).toList()
        );

        return dto;
    }
}