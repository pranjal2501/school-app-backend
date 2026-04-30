package com.example.school_management.admin.dto.dashboard;

import com.example.school_management.notify.dto.NotificationResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponseDto {
    private DashboardStatsDto stats;
    private List<TodayClassDto> todayClasses;
    private List<NotificationResponseDto> importantAlerts;
}