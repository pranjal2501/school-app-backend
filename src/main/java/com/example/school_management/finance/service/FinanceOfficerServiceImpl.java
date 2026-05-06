package com.example.school_management.finance.service;

import com.example.school_management.common.enums.Role;
import com.example.school_management.finance.dto.FinanceOfficerRequestDto;
import com.example.school_management.finance.dto.FinanceOfficerResponseDto;
import com.example.school_management.finance.entity.FinanceOfficer;
import com.example.school_management.finance.mapper.FinanceOfficerMapper;
import com.example.school_management.finance.repository.FinanceOfficerRepository;
import com.example.school_management.mail.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceOfficerServiceImpl implements FinanceOfficerService {

    private final FinanceOfficerRepository financeOfficerRepository;
    private final PasswordEncoder passwordEncoder;
    private final FinanceOfficerMapper financeOfficerMapper;
    private final EmailService emailService;

    @Override
    public FinanceOfficerResponseDto createFinanceOfficer(FinanceOfficerRequestDto dto) {
        FinanceOfficer officer = financeOfficerMapper.toEntity(dto);

        // Generate password same way as teacher
        String namePart = getSafeSubstring(officer.getLastName(), 3).toUpperCase();
        String mobilePart = getSafeSubstring(officer.getMobile(), 3);
        String aadharPart = getSafeSubstring(officer.getAadharNo(), 3);
        String rawPassword = namePart + mobilePart + aadharPart;

        officer.setPassword(passwordEncoder.encode(rawPassword));
        officer.setRole(Role.FINANCE);
        officer.setActive(true);

        FinanceOfficer saved = financeOfficerRepository.save(officer);

        // ADD THIS — send credentials email
        emailService.sendTeacherOnboardingEmail(
                saved.getEmail(),
                saved.getFirstName() + " " + saved.getLastName(),
                rawPassword
        );

        return financeOfficerMapper.toResponseDto(saved);
    }

    @Override
    public List<FinanceOfficerResponseDto> getAllFinanceOfficers() {
        return financeOfficerMapper.toResponseDtoList(financeOfficerRepository.findAll());
    }

    @Override
    public FinanceOfficerResponseDto getFinanceOfficerById(Long id) {
        return financeOfficerMapper.toResponseDto(findById(id));
    }

//    @Override
//    public FinanceOfficerResponseDto updateFinanceOfficer(Long id, FinanceOfficerRequestDto dto) {
//        FinanceOfficer officer = findById(id);
//
//        financeOfficerMapper.updateEntity(dto, officer);
//
//        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
//            officer.setPassword(passwordEncoder.encode(dto.getPassword()));
//        }
//
//        return financeOfficerMapper.toResponseDto(financeOfficerRepository.save(officer));
//    }

    @Override
    public void toggleStatus(Long id) {
        FinanceOfficer officer = findById(id);
        officer.setActive(!officer.isActive());
        financeOfficerRepository.save(officer);
    }

    @Override
    public void deleteFinanceOfficer(Long id) {
        financeOfficerRepository.delete(findById(id));
    }


    private FinanceOfficer findById(Long id) {
        return financeOfficerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Finance Officer not found with id: " + id));
    }

    //=============== HELPER METHOD ================================
    private String getSafeSubstring(String value, int length) {
        if (value == null) return "";
        return value.length() >= length ? value.substring(0, length) : value;
    }
}