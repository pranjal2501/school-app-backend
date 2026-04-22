package com.example.school_management.attendance.mapper;

import com.example.school_management.attendance.dto.config.AttendanceConfigRequestDto;
import com.example.school_management.attendance.dto.config.AttendanceConfigResponseDto;
import com.example.school_management.attendance.entity.AttendanceConfig;
import org.springframework.stereotype.Component;

@Component
public class ConfigMapper {

    public AttendanceConfig toEntity(AttendanceConfigRequestDto dto) {
        if (dto == null) {
            return null;
        }

        return AttendanceConfig.builder()
                .academicYear(dto.getAcademicYear())
                .attendanceType(dto.getAttendanceType())
                .studentWorkingDays(dto.getStudentWorkingDays())
                .studentMinAttendancePct(dto.getStudentMinAttendancePct())
                .allowLateMarks(dto.getAllowLateMarks())
                .lateMarksWindow(dto.getLateMarksWindow())
                .notifyParentOnAbsent(dto.getNotifyParentOnAbsent())
                .absentNotifyThreshold(dto.getAbsentNotifyThreshold())
                .minLeaveNoticeDays(dto.getMinLeaveNoticeDays())
                .build();
    }

    public AttendanceConfigResponseDto toResponseDto(AttendanceConfig entity) {
        if (entity == null) {
            return null;
        }

        return AttendanceConfigResponseDto.builder()
                .id(entity.getId())
                .academicYear(entity.getAcademicYear())
                .attendanceType(entity.getAttendanceType())
                .studentWorkingDays(entity.getStudentWorkingDays())
                .studentMinAttendancePct(entity.getStudentMinAttendancePct())
                .allowLateMarks(entity.getAllowLateMarks())
                .lateMarksWindow(entity.getLateMarksWindow())
                .notifyParentOnAbsent(entity.getNotifyParentOnAbsent())
                .absentNotifyThreshold(entity.getAbsentNotifyThreshold())
                .minLeaveNoticeDays(entity.getMinLeaveNoticeDays())
                .build();
    }

    public void updateEntity(AttendanceConfigRequestDto dto, AttendanceConfig entity) {
        if (dto != null && entity != null) {
            mapDtoToEntity(dto, entity);
        }
    }

    // Setters remain here because you cannot use a Builder to update an existing, attached Hibernate entity.
    public void mapDtoToEntity(AttendanceConfigRequestDto dto, AttendanceConfig entity) {
        entity.setAcademicYear(dto.getAcademicYear());
        entity.setAttendanceType(dto.getAttendanceType());
        entity.setStudentWorkingDays(dto.getStudentWorkingDays());
        entity.setStudentMinAttendancePct(dto.getStudentMinAttendancePct());
        entity.setAllowLateMarks(dto.getAllowLateMarks());
        entity.setLateMarksWindow(dto.getLateMarksWindow());
        entity.setNotifyParentOnAbsent(dto.getNotifyParentOnAbsent());
        entity.setAbsentNotifyThreshold(dto.getAbsentNotifyThreshold());
        entity.setMinLeaveNoticeDays(dto.getMinLeaveNoticeDays());
    }
}