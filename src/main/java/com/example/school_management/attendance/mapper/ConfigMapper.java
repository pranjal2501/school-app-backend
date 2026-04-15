package com.example.school_management.attendance.mapper;


import com.example.school_management.attendance.dto.config.AttendanceConfigRequestDto;
import com.example.school_management.attendance.dto.config.AttendanceConfigResponseDto;
import com.example.school_management.attendance.entity.AttendanceConfig;
import org.springframework.stereotype.Component;

@Component
public class ConfigMapper {

    public AttendanceConfig toEntity(AttendanceConfigRequestDto dto) {
        if (dto == null) return null;

        AttendanceConfig entity = new AttendanceConfig();
        mapDtoToEntity(dto, entity);
        return entity;
    }

    public AttendanceConfigResponseDto toResponseDto(AttendanceConfig entity) {
        if (entity == null) return null;

        AttendanceConfigResponseDto dto= new AttendanceConfigResponseDto();
        dto.setId(entity.getId());
        dto.setAcademicYear(entity.getAcademicYear());
        dto.setAttendanceType(entity.getAttendanceType());
        dto.setStudentWorkingDays(entity.getStudentWorkingDays());
        dto.setStudentMinAttendancePct(entity.getStudentMinAttendancePct());
        dto.setTeacherWorkingDays(entity.getTeacherWorkingDays());
        dto.setTeacherMinAttendancePct(entity.getTeacherMinAttendancePct());
        dto.setAllowLateMarks(entity.getAllowLateMarks());
        dto.setLateMarksWindow(entity.getLateMarksWindow());
        dto.setNotifyParentOnAbsent(entity.getNotifyParentOnAbsent());
        dto.setAbsentNotifyThreshold(entity.getAbsentNotifyThreshold());
        dto.setMinLeaveNoticeDays(entity.getMinLeaveNoticeDays());

        return dto;
    }

    public void updateEntity(AttendanceConfigRequestDto dto, AttendanceConfig entity) {
        if (dto != null && entity != null) {
            mapDtoToEntity(dto, entity);
        }
    }

    private void mapDtoToEntity(AttendanceConfigRequestDto dto, AttendanceConfig entity) {
        entity.setAcademicYear(dto.getAcademicYear());
        entity.setAttendanceType(dto.getAttendanceType());
        entity.setStudentWorkingDays(dto.getStudentWorkingDays());
        entity.setStudentMinAttendancePct(dto.getStudentMinAttendancePct());
        entity.setTeacherWorkingDays(dto.getTeacherWorkingDays());
        entity.setTeacherMinAttendancePct(dto.getTeacherMinAttendancePct());
        entity.setAllowLateMarks(dto.getAllowLateMarks());
        entity.setLateMarksWindow(dto.getLateMarksWindow());
        entity.setNotifyParentOnAbsent(dto.getNotifyParentOnAbsent());
        entity.setAbsentNotifyThreshold(dto.getAbsentNotifyThreshold());
        entity.setMinLeaveNoticeDays(dto.getMinLeaveNoticeDays());
    }
}
