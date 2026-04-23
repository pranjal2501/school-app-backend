package com.example.school_management.parent.mapper;


import com.example.school_management.notify.dto.ParentNotificationResponseDto;
import com.example.school_management.parent.dto.*;
import com.example.school_management.parent.entity.Parent;
import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.student.entity.Student;
import com.example.school_management.teacher.entity.Teacher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ParentMapper {

    // DTO → Entity
    public static Parent toEntity(ParentCreateRequestDto dto) {
        if (dto == null) return null;

        Parent parent = new Parent();

        parent.setFatherFirstName(dto.getFatherFirstName());
        parent.setFatherMiddleName(dto.getFatherMiddleName());
        parent.setFatherLastName(dto.getFatherLastName());
        parent.setFatherOccupation(dto.getFatherOccupation());

        parent.setMotherFirstName(dto.getMotherFirstName());
        parent.setMotherMiddleName(dto.getMotherMiddleName());
        parent.setMotherLastName(dto.getMotherLastName());
        parent.setMotherOccupation(dto.getMotherOccupation());

        parent.setMobilePrimary(dto.getMobilePrimary());
        parent.setEmail(dto.getEmail());
        parent.setPassword(dto.getPassword());

        parent.setAadharNo(dto.getAadharNo());
        parent.setAadharOwnerType(dto.getAadharOwnerType());

        return parent;
    }

    // Entity → Response DTO
    public static ParentResponseDto toResponseDto(Parent parent) {
        if (parent == null) return null;

        ParentResponseDto dto = new ParentResponseDto();

        dto.setId(parent.getId());

        dto.setFatherFirstName(parent.getFatherFirstName());
        dto.setFatherMiddleName(parent.getFatherMiddleName());
        dto.setFatherLastName(parent.getFatherLastName());
        dto.setFatherOccupation(parent.getFatherOccupation());

        dto.setMotherFirstName(parent.getMotherFirstName());
        dto.setMotherLastName(parent.getMotherLastName());
        dto.setMotherOccupation(parent.getMotherOccupation());

        dto.setGuardianFirstName(parent.getGuardianFirstName());
        dto.setGuardianMiddleName(parent.getGuardianMiddleName());
        dto.setGuardianLastName(parent.getGuardianLastName());

        dto.setMobilePrimary(parent.getMobilePrimary());
        dto.setEmail(parent.getEmail());
        dto.setAadharNo(parent.getAadharNo());

        dto.setStudents(
                ParentMapper.toLiteDtoList(parent.getStudents())
        );

        return dto;
    }

    public static StudentLiteDto toLiteDto(Student student) {
        if (student == null) return null;

        StudentLiteDto dto = new StudentLiteDto();
        dto.setId(student.getId());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());

        return dto;
    }

    public static List<StudentLiteDto> toLiteDtoList(List<Student> students) {
        if (students == null) return new ArrayList<>();

        return students.stream()
                .map(ParentMapper::toLiteDto)
                .collect(Collectors.toList());
    }

//    public ChildDetailsResponseDto toChildDetailsResponseDto(Student child, float attendancePercent, String academicYear) {
//
//        SchoolClass assignedClass = child.getAssignedClass();
//        Teacher classTeacher = assignedClass != null ? assignedClass.getClassTeacher() : null;
//
//        return ChildDetailsResponseDto.builder()
//                .name(child.getFirstName() + " " + child.getLastName())
//                .schoolClass(assignedClass != null ? assignedClass.getClassName() : "N/A")
//                .rollNo(child.getRollNumber())
//                .classTeacher(classTeacher != null
//                        ? classTeacher.getFirstName() + " " + classTeacher.getLastName()
//                        : "N/A")
//                .academicYear(academicYear)
//                .attendancePercent(attendancePercent)
//                .build();
//    }

    public ChildSummaryResponseDto toSummaryDto(Student child) {
        return ChildSummaryResponseDto.builder()
                .registrationId(child.getRegId())
                .name(child.getFirstName() + " " + child.getLastName())
                .schoolClass(child.getAssignedClass() != null
                        ? child.getAssignedClass().getClassName()
                        : "N/A")
                .rollNo(child.getRollNumber())
                .build();
    }

    public ParentsDashboardResponseDto toDashboardResponseDto(
            Student child,
            Float attendancePercent,
            Integer pendingAssignments,
            List<ParentNotificationResponseDto> recentNotifications) {

        SchoolClass assignedClass = child.getAssignedClass();

        return ParentsDashboardResponseDto.builder()
                .studentName(child.getFirstName() + " " + child.getLastName())
                .schoolClass(assignedClass != null ? assignedClass.getClassName() : "N/A")
                .attendancePercent(attendancePercent)
                .pendingAssignments(pendingAssignments)
                .recentNotifications(recentNotifications)
                .build();
    }
}