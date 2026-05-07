package com.example.school_management.finance.service;

import com.example.school_management.finance.dto.FinanceOfficerRequestDto;
import com.example.school_management.finance.dto.FinanceOfficerResponseDto;

import java.util.List;

public interface FinanceOfficerService {

    FinanceOfficerResponseDto createFinanceOfficer(FinanceOfficerRequestDto dto);

    List<FinanceOfficerResponseDto> getAllFinanceOfficers();

    FinanceOfficerResponseDto getFinanceOfficerById(Long id);

   // FinanceOfficerResponseDto updateFinanceOfficer(Long id, FinanceOfficerRequestDto dto);

    void toggleStatus(Long id);

    void deleteFinanceOfficer(Long id);
}