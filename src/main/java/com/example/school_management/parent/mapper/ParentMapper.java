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

    // ── DTO → Entity ──
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

    // ── Entity → Response DTO ──
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
        dto.setStudents(ParentMapper.toLiteDtoList(parent.getStudents()));
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
        return students.stream().map(ParentMapper::toLiteDto).collect(Collectors.toList());
    }

    // 🚀 FIXED: Fully maps all 25+ fields to the UI DTO
    public ChildDetailsResponseDto toChildDetailsResponseDto(Student child, float attendancePercent, String academicYear) {
        SchoolClass assignedClass = child.getAssignedClass();
        Teacher classTeacher = assignedClass != null ? assignedClass.getClassTeacher() : null;

        return ChildDetailsResponseDto.builder()
                .regId(child.getRegId())
                .academicYear(academicYear != null ? academicYear : "N/A")
                .attendancePercentage(attendancePercent)
                .firstName(child.getFirstName())
                .middleName(child.getMiddleName())
                .lastName(child.getLastName())
                .gender(child.getGender() != null ? child.getGender().name() : null)
                .dob(child.getDob())
                .bloodGroup(child.getBloodGroup() != null ? child.getBloodGroup().name() : null)
                .aadharNo(child.getAadharNo())
                .nationality(child.getNationality())
                .religion(child.getReligion() != null ? child.getReligion().name() : null)
                .category(child.getCategory() != null ? child.getCategory().name() : null)
                .standard(child.getStandard())
                .rollNumber(child.getRollNumber())
                .className(assignedClass != null ? assignedClass.getClassName() : "Unassigned")
                .classTeacherName(classTeacher != null ? classTeacher.getFirstName() + " " + classTeacher.getLastName() : null)
                .classTeacherEmail(classTeacher != null ? classTeacher.getEmail() : null)
                .classTeacherMobile(classTeacher != null ? classTeacher.getMobile() : null)
                .classTeacherDegree(classTeacher != null && classTeacher.getDegreeType() != null ? classTeacher.getDegreeType().name() : null)
                .previousSchool(child.getPreviousSchool())
                .previousPercentage(child.getPreviousPercentage())
                .birthCertificateUrl(child.getBirthCertificateUrl())
                .aadharPhotoUrl(child.getAadharPhotoUrl())
                .previousMarksheetUrl(child.getPreviousMarksheetUrl())
                .passportPhotoUrl(child.getPassportPhotoUrl())
                .leavingCertificateUrl(child.getLeavingCertificateUrl())
                .casteCertificateUrl(child.getCasteCertificateUrl())
                .incomeCertificateUrl(child.getIncomeCertificateUrl())
                .build();
    }

    // 🚀 FIXED: Null-safe parent ID extraction
    public ChildSummaryResponseDto toSummaryDto(Student child) {
        return ChildSummaryResponseDto.builder()
                .registrationId(child.getRegId())
                .name(child.getFirstName() + " " + child.getLastName())
                .schoolClass(child.getAssignedClass() != null ? child.getAssignedClass().getClassName() : "Unassigned")
                .rollNo(child.getRollNumber())
                .classId(child.getAssignedClass() != null ? child.getAssignedClass().getId() : null)
                .parentId(child.getParent() != null ? child.getParent().getId() : null)
                .build();
    }

    // 🚀 FIXED: Synchronized with ParentServiceImpl fields
    public ParentsDashboardResponseDto toDashboardResponseDto(
            Student child,
            Parent parent,
            boolean isClassAllocated,
            boolean isProfileComplete,
            Float attendancePercent,
            Integer pendingAssignments,
            List<ParentNotificationResponseDto> recentNotifications) {

        return ParentsDashboardResponseDto.builder()
                .studentId(child.getId())
                .parentName(parent.getFatherFirstName() + " " + parent.getFatherLastName())
                .studentName(child.getFirstName() + " " + child.getLastName())
                .schoolClass(isClassAllocated ? child.getAssignedClass().getClassName() : "Unassigned")
                .isClassAllocated(isClassAllocated)
                .isProfileComplete(isProfileComplete)
                .attendancePercent(attendancePercent)
                .pendingAssignments(pendingAssignments)
                .recentNotifications(recentNotifications)
                .build();
    }

    public ParentProfileResponseDto toProfileDto(Parent parent) {
        if (parent == null) return null;

        String fatherName = String.format("%s %s %s",
                parent.getFatherFirstName(),
                parent.getFatherMiddleName() != null ? parent.getFatherMiddleName() : "",
                parent.getFatherLastName()).trim().replaceAll(" +", " ");

        String motherName = String.format("%s %s %s",
                parent.getMotherFirstName(),
                parent.getMotherMiddleName() != null ? parent.getMotherMiddleName() : "",
                parent.getMotherLastName()).trim().replaceAll(" +", " ");

        String fullAddress = String.format("%s, %s, %s, %s - %s",
                        parent.getAddressLine1() != null ? parent.getAddressLine1() : "",
                        parent.getAddressLine2() != null ? parent.getAddressLine2() : "",
                        parent.getCity() != null ? parent.getCity() : "",
                        parent.getState() != null ? parent.getState() : "",
                        parent.getPincode() != null ? parent.getPincode() : "")
                .replaceAll("^, |, $", "")
                .replaceAll(", ,", ", ");

        return ParentProfileResponseDto.builder()
                .parentId(parent.getId())
                .fatherFullName(fatherName)
                .fatherOccupation(parent.getFatherOccupation())
                .motherFullName(motherName)
                .motherOccupation(parent.getMotherOccupation())
                .email(parent.getEmail())
                .mobilePrimary(parent.getMobilePrimary())
                .mobileAlternate(parent.getMobileAlternate())
                .emergencyContactName(parent.getEmergencyContactName())     // 🚀 ADDED
                .emergencyContactNumber(parent.getEmergencyContactNumber()) // 🚀 ADDED
                .fullAddress(fullAddress.length() > 5 ? fullAddress : "Address not provided")
                .aadharNo(parent.getAadharNo())
                .aadharOwnerType(parent.getAadharOwnerType() != null ? parent.getAadharOwnerType().name() : "N/A")
                .build();
    }
}