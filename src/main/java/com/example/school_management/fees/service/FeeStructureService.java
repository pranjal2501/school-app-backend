package com.example.school_management.fees.service;

import com.example.school_management.admin.entity.Admin;
import com.example.school_management.admin.repository.AdminRepository;
import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.exception.DuplicateResourceException;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.fees.dto.requestDto.CreateFeeStructureRequestDto;
import com.example.school_management.fees.dto.responseDto.FeeComponentResponseDto;
import com.example.school_management.fees.dto.responseDto.FeeStructureResponseDto;
import com.example.school_management.fees.dto.responseDto.InstallmentPlanResponseDto;
import com.example.school_management.fees.dto.responseDto.InstallmentScheduleResponseDto;
import com.example.school_management.fees.entity.FeeComponent;
import com.example.school_management.fees.entity.FeeStructure;
import com.example.school_management.fees.entity.InstallmentPlan;
import com.example.school_management.fees.entity.InstallmentSchedule;
import com.example.school_management.fees.repository.FeeAllocationRepository;
import com.example.school_management.fees.repository.FeeStructureRepository;
import com.example.school_management.finance.entity.FinanceOfficer;
import com.example.school_management.finance.repository.FinanceOfficerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FeeStructureService {

    private final FeeStructureRepository feeStructureRepository;
    private final FeeAllocationRepository feeAllocationRepository;
    private final RoleVerifier roleVerifier;
    private final AdminRepository adminRepository;
    private final FinanceOfficerRepository financeOfficerRepository;

    // ─── Create ────────────────────────────────────────────────

    public FeeStructureResponseDto createFeeStructure(CreateFeeStructureRequestDto request) {

        if (feeStructureRepository.existsByStandardAndAcademicYear(
                request.getStandard(), request.getAcademicYear())) {
            throw new DuplicateResourceException(
                    "Fee structure already exists for standard " + request.getStandard()
                            + " and year " + request.getAcademicYear());
        }

        String userName= roleVerifier.getCurrentUsername();
        Admin admin=null;
        FinanceOfficer financeOfficer= null;

        if(adminRepository.findByUsername(userName).isPresent()) {
            admin = adminRepository.findByUsername(userName).get();

        } else if (financeOfficerRepository.findByEmail(userName).isPresent()) {
            financeOfficer= financeOfficerRepository.findByEmail(userName).get();
        }
        FeeStructure feeStructure = new FeeStructure();
        feeStructure.setStandard(request.getStandard());
        feeStructure.setAcademicYear(request.getAcademicYear());
        feeStructure.setTotalAmount(request.getTotalAmount());
        feeStructure.setDescription(request.getDescription());

        // Map components
        List<FeeComponent> components = request.getComponents().stream().map(dto -> {
            FeeComponent component = new FeeComponent();
            component.setFeeType(dto.getFeeType());
            component.setAmount(dto.getAmount());
            component.setRemarks(dto.getRemarks());
            component.setFeeStructure(feeStructure);
            return component;
        }).toList();
        feeStructure.setComponents(components);

        // Map installment plans
        List<InstallmentPlan> plans = request.getInstallmentPlans().stream().map(planDto -> {
            InstallmentPlan plan = new InstallmentPlan();
            plan.setPlanName(planDto.getPlanName());
            plan.setNumberOfInstallments(planDto.getNumberOfInstallments());
            plan.setDescription(planDto.getDescription());
            plan.setFeeStructure(feeStructure);

            List<InstallmentSchedule> schedules = planDto.getSchedules().stream().map(schedDto -> {
                InstallmentSchedule schedule = new InstallmentSchedule();
                schedule.setInstallmentNumber(schedDto.getInstallmentNumber());
                schedule.setAmount(schedDto.getAmount());
                schedule.setDueDate(schedDto.getDueDate());
                schedule.setRemarks(schedDto.getRemarks());
                schedule.setInstallmentPlan(plan);
                return schedule;
            }).toList();
            plan.setSchedules(schedules);

            return plan;
        }).toList();
        feeStructure.setInstallmentPlans(plans);
        feeStructure.setCreatedBy(admin);
        feeStructure.setCreatedByFinanceOfficer(financeOfficer);
        FeeStructure saved = feeStructureRepository.save(feeStructure);
        return mapToFeeStructureResponse(saved);
    }

    // ─── Read ───────────────────────────────────────────────────

    public FeeStructureResponseDto getFeeStructureById(Long id) {
        FeeStructure feeStructure = feeStructureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fee structure not found with id: " + id));
        return mapToFeeStructureResponse(feeStructure);
    }

    public FeeStructureResponseDto getFeeStructureByStandardAndYear(String standard, String academicYear) {
        FeeStructure feeStructure = feeStructureRepository
                .findByStandardAndAcademicYear(standard, academicYear)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Fee structure not found for standard " + standard + " and year " + academicYear));
        return mapToFeeStructureResponse(feeStructure);
    }

    public List<FeeStructureResponseDto> getAllFeeStructures() {
        return feeStructureRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToFeeStructureResponse)
                .toList();
    }

    // ─── Update ─────────────────────────────────────────────────

    public FeeStructureResponseDto updateFeeStructure(Long id, CreateFeeStructureRequestDto request) throws BadRequestException {
        FeeStructure existing = feeStructureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fee structure not found with id: " + id));

        if (feeAllocationRepository.existsByFeeStructure_Id(id)) {
            throw new BadRequestException(
                    "Cannot update fee structure — allocations already exist for this structure.");
        }

        existing.setTotalAmount(request.getTotalAmount());
        existing.setDescription(request.getDescription());

        // Replace components
        existing.getComponents().clear();
        request.getComponents().forEach(dto -> {
            FeeComponent component = new FeeComponent();
            component.setFeeType(dto.getFeeType());
            component.setAmount(dto.getAmount());
            component.setRemarks(dto.getRemarks());
            component.setFeeStructure(existing);
            existing.getComponents().add(component);
        });

        // Replace installment plans
        existing.getInstallmentPlans().clear();
        request.getInstallmentPlans().forEach(planDto -> {
            InstallmentPlan plan = new InstallmentPlan();
            plan.setPlanName(planDto.getPlanName());
            plan.setNumberOfInstallments(planDto.getNumberOfInstallments());
            plan.setDescription(planDto.getDescription());
            plan.setFeeStructure(existing);

            planDto.getSchedules().forEach(schedDto -> {
                InstallmentSchedule schedule = new InstallmentSchedule();
                schedule.setInstallmentNumber(schedDto.getInstallmentNumber());
                schedule.setAmount(schedDto.getAmount());
                schedule.setDueDate(schedDto.getDueDate());
                schedule.setRemarks(schedDto.getRemarks());
                schedule.setInstallmentPlan(plan);
                plan.getSchedules().add(schedule);
            });

            existing.getInstallmentPlans().add(plan);
        });

        FeeStructure saved = feeStructureRepository.save(existing);
        return mapToFeeStructureResponse(saved);
    }

    // ─── Delete ─────────────────────────────────────────────────

    public void deleteFeeStructure(Long id) throws BadRequestException {
        FeeStructure feeStructure = feeStructureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fee structure not found with id: " + id));

        if (feeAllocationRepository.existsByFeeStructure_Id(id)) {
            throw new BadRequestException(
                    "Cannot delete fee structure — allocations already exist for this structure.");
        }

        feeStructureRepository.delete(feeStructure);
    }

    // ─── Mapper ─────────────────────────────────────────────────

    private FeeStructureResponseDto mapToFeeStructureResponse(FeeStructure fs) {
        FeeStructureResponseDto dto = new FeeStructureResponseDto();
        dto.setId(fs.getId());
        dto.setStandard(fs.getStandard());
        dto.setAcademicYear(fs.getAcademicYear());
        dto.setTotalAmount(fs.getTotalAmount());
        dto.setDescription(fs.getDescription());
        dto.setCreatedAt(fs.getCreatedAt());

        dto.setComponents(fs.getComponents().stream().map(c -> {
            FeeComponentResponseDto cdto = new FeeComponentResponseDto();
            cdto.setId(c.getId());
            cdto.setFeeType(c.getFeeType());
            cdto.setFeeTypeDisplayName(c.getFeeType().getDisplayName()); // assumes enum has getDisplayName()
            cdto.setAmount(c.getAmount());
            cdto.setRemarks(c.getRemarks());
            return cdto;
        }).toList());

        dto.setInstallmentPlans(fs.getInstallmentPlans().stream().map(p -> {
            InstallmentPlanResponseDto pdto = new InstallmentPlanResponseDto();
            pdto.setId(p.getId());
            pdto.setPlanName(p.getPlanName());
            pdto.setNumberOfInstallments(p.getNumberOfInstallments());
            pdto.setDescription(p.getDescription());

            pdto.setSchedules(p.getSchedules().stream().map(s -> {
                InstallmentScheduleResponseDto sdto = new InstallmentScheduleResponseDto();
                sdto.setId(s.getId());
                sdto.setInstallmentNumber(s.getInstallmentNumber());
                sdto.setAmount(s.getAmount());
                sdto.setDueDate(s.getDueDate());
                sdto.setRemarks(s.getRemarks());
                return sdto;
            }).toList());

            return pdto;
        }).toList());

        return dto;
    }
}