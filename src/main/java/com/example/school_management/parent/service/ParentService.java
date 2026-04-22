package com.example.school_management.parent.service;

import com.example.school_management.parent.dto.ChildDetailsResponseDto;
import com.example.school_management.parent.dto.ChildSummaryResponseDto;
import com.example.school_management.parent.dto.ParentsDashboardResponseDto;
import com.example.school_management.parent.entity.Parent;
import com.example.school_management.student.entity.Student;

import java.util.List;

public interface ParentService {

    ParentsDashboardResponseDto getDashboard();

    ParentsDashboardResponseDto getDashboard(String regId);

    List<ChildSummaryResponseDto> getChildList();

    ChildDetailsResponseDto getChildDetails(String regId);
}