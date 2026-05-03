package com.example.school_management.employee.service;

import com.example.school_management.employee.dto.EmployeeResponseDto;
import com.example.school_management.employee.entity.Employee;
import com.example.school_management.employee.mapper.EmployeeMapper;
import com.example.school_management.finance.repository.FinanceOfficerRepository;
import com.example.school_management.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final TeacherRepository teacherRepository;
    private final FinanceOfficerRepository financeOfficerRepository;
    private final EmployeeMapper employeeMapper;

    public List<EmployeeResponseDto> getAllEmployees() {
        List<Employee> allEmployees = new ArrayList<>();

        allEmployees.addAll(teacherRepository.findAll());
        allEmployees.addAll(financeOfficerRepository.findAll());

        return allEmployees.stream()
                .map(employeeMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}